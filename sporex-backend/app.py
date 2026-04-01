import os
import random
import shutil
import uuid
from datetime import datetime, timezone, timedelta
from pathlib import Path

import requests
from dotenv import load_dotenv
from fastapi import FastAPI, Header, HTTPException, UploadFile, File, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles
from passlib.context import CryptContext
from PIL import Image
from pydantic import BaseModel, EmailStr
from pymongo import MongoClient
from ultralytics import YOLO


# ---------- Load environment ----------
env_path = Path(__file__).parent / ".env"
load_dotenv(dotenv_path=env_path)

print("DEBUG ENV CHECK")


# ---------- Password hashing utils ----------
# Use PBKDF2-SHA256 instead of bcrypt to avoid backend issues
pwd_context = CryptContext(
    schemes=["pbkdf2_sha256"],
    deprecated="auto",
)


def hash_password(password: str) -> str:
    return pwd_context.hash(password)


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)


# ---------- Config ----------
MONGO_URI = os.getenv("MONGODB_URI")
DB_NAME = os.getenv("MONGODB_DB_NAME")
DEVICE_INGEST_TOKEN = os.getenv("DEVICE_INGEST_TOKEN")
RESEND_API_KEY = os.getenv("RESEND_API_KEY")

if not MONGO_URI or not DB_NAME:
    raise RuntimeError("Missing MONGODB_URI or MONGODB_DB_NAME in environment")

client = MongoClient(MONGO_URI)
db = client[DB_NAME]
users_col = db["users"]
readings_col = db["sensor_readings"]
products_col = db["products"]
posts_col = db["posts"]

# ✅ NEW: scans collection
scans_col = db["image_scans"]


# ---------- FastAPI app ----------
app = FastAPI(
    title="SporeX Backend",
    version="0.2.0",
)

# CORS – Android isn’t a browser, but this also helps if you later add a web UI
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ---------- Image/model setup ----------
BASE_DIR = Path(__file__).parent
UPLOAD_DIR = BASE_DIR / "uploads"
ANNOTATED_DIR = BASE_DIR / "annotated"
UPLOAD_DIR.mkdir(exist_ok=True)
ANNOTATED_DIR.mkdir(exist_ok=True)

app.mount("/static", StaticFiles(directory=str(ANNOTATED_DIR)), name="static")


MODEL_PATH = BASE_DIR / "models" / "best.pt"

if not MODEL_PATH.exists():
    print(f"WARNING: Model file not found at {MODEL_PATH}")
    model = None
else:
    model = YOLO(str(MODEL_PATH))


# ---------- Pydantic models ----------
class RegisterBody(BaseModel):
    email: EmailStr
    password: str
    username: str | None = None
    name: str | None = None


class LoginBody(BaseModel):
    email: EmailStr
    password: str


class BasicResponse(BaseModel):
    success: bool
    message: str


class ReadingBody(BaseModel):
    device_id: str
    co2: int
    temp_c: float
    humidity: float
    ts: int | None = None


class ProductDto(BaseModel):
    id: str
    name: str
    sustainable: bool = True
    best_for: str
    steps: list[str]
    warning: str | None = None


class ReplyDto(BaseModel):
    user_name: str
    content: str
    created_at: datetime | None = None


class PostDto(BaseModel):
    user_name: str
    post_name: str
    content: str
    created_at: datetime | None = None
    replies: list[ReplyDto] = []


class PostCreateBody(BaseModel):
    user_name: str
    post_name: str
    content: str


class ReplyCreateBody(BaseModel):
    user_name: str
    content: str

# ------------- meghan's settings endpoints -------------

class SettingsModel(BaseModel):
    dark_mode: bool
    notifications_enabled: bool
    data_personalisation: bool
    app_customisation: dict


class UpdateSettingsBody(BaseModel):
    email: EmailStr
    settings: SettingsModel

@app.get("/api/settings/{email}")
async def get_settings(email: str):
    user = users_col.find_one({"email": email})

    if not user:
        return JSONResponse(
            status_code=404,
            content={"success": False, "message": "User not found"},
        )

    return {
        "success": True,
        "settings": user.get("settings", {})
    }

@app.put("/api/settings")
async def update_settings(body: UpdateSettingsBody):
    result = users_col.update_one(
        {"email": body.email},
        {"$set": {"settings": body.settings.dict()}}
    )

    if result.matched_count == 0:
        return JSONResponse(
            status_code=404,
            content={"success": False, "message": "User not found"},
        )

    return {"success": True, "message": "Settings updated"}

    
# prediction response models
class DetectionBox(BaseModel):
    class_name: str
    confidence: float
    x1: float
    y1: float
    x2: float
    y2: float


class PredictResponse(BaseModel):
    success: bool
    mould_detected: bool
    max_confidence: float | None = None
    detections: list[DetectionBox]
    image_url: str | None = None
    message: str


# ---------- Helper functions ----------
def generate_otp():
    return str(random.randint(100000, 999999))


def send_otp_email(to_email: str, otp: str):
    print("DEBUG RESEND KEY:", "SET" if RESEND_API_KEY else "NOT SET")

    if not RESEND_API_KEY:
        print("❌ RESEND_API_KEY missing")
        return

    try:
        response = requests.post(
            "https://api.resend.com/emails",
            headers={
                "Authorization": f"Bearer {RESEND_API_KEY}",
                "Content-Type": "application/json",
            },
            json={
                "from": "SporeX <onboarding@resend.dev>",
                "to": [to_email],
                "subject": "SporeX Email Verification",
                "html": f"<p>Your verification code is: <strong>{otp}</strong></p>",
            },
            timeout=20,
        )

        print("Email response:", response.text)

    except Exception as e:
        print("❌ Email error:", e)


def validate_image_file(saved_path: Path):
    try:
        with Image.open(saved_path) as img:
            img.verify()
    except Exception:
        if saved_path.exists():
            saved_path.unlink(missing_ok=True)
        raise HTTPException(
            status_code=400, detail="Uploaded file is not a valid image"
        )

# ---------- Routes ----------
@app.get("/api/health", response_model=BasicResponse)
async def health_check():
    return {"success": True, "message": "Backend running"}


@app.post("/api/register")
async def register(body: RegisterBody):
    if users_col.find_one({"email": body.email}):
        return JSONResponse(
            status_code=409,
            content={"success": False, "message": "User already exists"},
        )

    username = body.username or body.email.split("@")[0]
    password_hash = hash_password(body.password)
    otp = generate_otp()

    user_doc = {
        "email": body.email,
        "username": username,
        "password_hash": password_hash,
        "role": "member",
        "status": "active",
        "is_verified": False,
        "otp": otp,
        "otp_expiry": datetime.now(timezone.utc) + timedelta(minutes=10),
        "created_at": datetime.now(timezone.utc),
        "settings": {
            "dark_mode": False,
            "notifications_enabled": True,
            "data_personalisation": True,
            "app_customisation": {"accent_color": "green", "layout_style": "default"},
        },
    }

    if body.name:
        user_doc["name"] = body.name

    users_col.insert_one(user_doc)
    send_otp_email(body.email, otp)

    return {"success": True, "message": "User registered"}


@app.post("/api/login")
async def login(body: LoginBody):
    user = users_col.find_one({"email": body.email})

    if not user:
        return JSONResponse(
            status_code=401,
            content={"success": False, "message": "Invalid credentials"},
        )

    if not verify_password(body.password, user["password_hash"]):
        return JSONResponse(
            status_code=401,
            content={"success": False, "message": "Invalid credentials"},
        )

    if not user.get("is_verified", True):
        return JSONResponse(
            status_code=403,
            content={"success": False, "message": "Please verify your email"},
        )

    return {
        "success": True,
        "message": "Login OK",
        "user": {
            "email": user.get("email"),
            "username": user.get("username"),
            "name": user.get("name"),
        },
    }


@app.get("/api/settings/{email}")
async def get_settings(email: str):
    user = users_col.find_one({"email": email})

    if not user:
        return JSONResponse(
            status_code=404,
            content={"success": False, "message": "User not found"},
        )

    return {"success": True, "settings": user.get("settings", {})}


@app.put("/api/settings")
async def update_settings(body: UpdateSettingsBody):
    result = users_col.update_one(
        {"email": body.email}, {"$set": {"settings": body.settings.dict()}}
    )

    if result.matched_count == 0:
        return JSONResponse(
            status_code=404,
            content={"success": False, "message": "User not found"},
        )

    return {"success": True, "message": "Settings updated"}


@app.post("/api/readings", response_model=BasicResponse)
async def ingest_reading(
    body: ReadingBody,
    x_device_token: str | None = Header(default=None),
):
    if not DEVICE_INGEST_TOKEN or x_device_token != DEVICE_INGEST_TOKEN:
        raise HTTPException(status_code=401, detail="Unauthorized device")

    device_dt = (
        datetime.fromtimestamp(body.ts, tz=timezone.utc)
        if body.ts is not None
        else datetime.now(timezone.utc)
    )

    doc = {
        "device_id": body.device_id,
        "co2": body.co2,
        "temp_c": body.temp_c,
        "humidity": body.humidity,
        "device_ts": device_dt,
        "created_at": datetime.now(timezone.utc),
    }

    readings_col.insert_one(doc)
    return {"success": True, "message": "Reading stored"}


# ---------- Products ----------
@app.get("/api/products")
async def list_products():
    products = list(products_col.find({}, {"_id": 0}))
    return products


@app.get("/api/products/{product_id}")
def get_product(product_id: str):
    product = products_col.find_one({"id": product_id}, {"_id": 0})
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product


# ---------- Posts ----------
@app.post("/api/posts", response_model=BasicResponse)
async def create_post(body: PostCreateBody):
    """Create a new post"""
    post_doc = {
        "user_name": body.user_name,
        "post_name": body.post_name,
        "content": body.content,
        "created_at": datetime.now(timezone.utc),
        "replies": []
    }
    result = posts_col.insert_one(post_doc)
    return {
        "success": True,
        "message": f"Post created with ID: {result.inserted_id}"
    }

@app.get("/api/posts")
async def list_posts():
    posts = []
    for post in posts_col.find():
        replies = []
        for reply in post.get("replies", []):
            replies.append({
                "user_name": reply.get("user_name", ""),
                "content": reply.get("content", ""),
                "created_at": reply.get("created_at").isoformat() if reply.get("created_at") else None
            })

        posts.append({
            "id": str(post["_id"]),
            "user_name": post.get("user_name", ""),
            "post_name": post.get("post_name", ""),
            "content": post.get("content", ""),
            "created_at": post.get("created_at").isoformat() if post.get("created_at") else None,
            "replies": replies
        })
    return posts

@app.get("/api/posts/{post_id}")
async def get_post(post_id: str):
    from bson import ObjectId

    try:
        post = posts_col.find_one({"_id": ObjectId(post_id)})
    except:
        raise HTTPException(status_code=400, detail="Invalid post ID")

    if not post:
        raise HTTPException(status_code=404, detail="Post not found")

    replies = []
    for reply in post.get("replies", []):
        replies.append({
            "user_name": reply.get("user_name", ""),
            "content": reply.get("content", ""),
            "created_at": reply.get("created_at").isoformat() if reply.get("created_at") else None
        })

    return {
        "id": str(post["_id"]),
        "user_name": post.get("user_name", ""),
        "post_name": post.get("post_name", ""),
        "content": post.get("content", ""),
        "created_at": post.get("created_at").isoformat() if post.get("created_at") else None,
        "replies": replies
    }

@app.post("/api/posts/{post_id}/replies", response_model=BasicResponse)
async def add_reply(post_id: str, body: ReplyCreateBody):
    from bson import ObjectId

    try:
        post_id_obj = ObjectId(post_id)
    except:
        raise HTTPException(status_code=400, detail="Invalid post ID")

    reply_doc = {
        "user_name": body.user_name,
        "content": body.content,
        "created_at": datetime.now(timezone.utc)
    }
    
    result = posts_col.update_one(
        {"_id": post_id_obj},
        {"$push": {"replies": reply_doc}}
    )
    
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Post not found")

    return {"success": True, "message": "Reply added to post"}


# ---------- AI image prediction ----------
@app.post("/api/predict", response_model=PredictResponse)
async def predict_image(
    file: UploadFile = File(...),
    email: str | None = Form(default=None),
):
    print("DEBUG content_type:", file.content_type)
    print("DEBUG filename:", file.filename)
    print("DEBUG email:", email)

    if model is None:
        raise HTTPException(status_code=500, detail="Model is not loaded on the server")

    allowed_types = {"image/jpeg", "image/jpg", "image/png"}
    if file.content_type not in allowed_types:
        raise HTTPException(
            status_code=400, detail="Only JPG and PNG images are allowed"
        )

    # Optional simple size limit: 5MB
    file_bytes = await file.read()
    if len(file_bytes) > 5 * 1024 * 1024:
        raise HTTPException(status_code=400, detail="Image too large. Max size is 5MB.")

    original_name = file.filename or "upload.jpg"
    ext = Path(original_name).suffix.lower()
    if ext not in {".jpg", ".jpeg", ".png"}:
        ext = ".jpg"

    file_id = str(uuid.uuid4())
    saved_path = UPLOAD_DIR / f"{file_id}{ext}"

    with saved_path.open("wb") as buffer:
        buffer.write(file_bytes)

    try:
        validate_image_file(saved_path)
    except Exception:
        if saved_path.exists():
            saved_path.unlink(missing_ok=True)
        raise

    try:
        results = model.predict(source=str(saved_path), save=False, conf=0.25)

        result = results[0]

        detections = []
        max_confidence = None

        if result.boxes is not None:
            for box in result.boxes:
                cls_id = int(box.cls[0].item())
                conf = float(box.conf[0].item())
                x1, y1, x2, y2 = box.xyxy[0].tolist()

                detection = {
                    "class_name": result.names[cls_id],
                    "confidence": round(conf, 4),
                    "x1": round(x1, 2),
                    "y1": round(y1, 2),
                    "x2": round(x2, 2),
                    "y2": round(y2, 2),
                }
                detections.append(detection)

                if max_confidence is None or conf > max_confidence:
                    max_confidence = conf

        mould_detected = len(detections) > 0

        # Save annotated image
        plotted = result.plot()  # numpy array in BGR
        annotated_filename = f"{file_id}.jpg"
        annotated_path = ANNOTATED_DIR / annotated_filename
        Image.fromarray(plotted[:, :, ::-1]).save(annotated_path)

        image_url = f"/static/{annotated_filename}"

        scan_doc = {
            "user_email": email,
            "original_filename": original_name,
            "stored_filename": saved_path.name,
            "annotated_filename": annotated_filename,
            "content_type": file.content_type,
            "mould_detected": mould_detected,
            "max_confidence": round(max_confidence, 4)
            if max_confidence is not None
            else None,
            "detections": detections,
            "created_at": datetime.now(timezone.utc),
        }
        scans_col.insert_one(scan_doc)

        return {
            "success": True,
            "mould_detected": mould_detected,
            "max_confidence": round(max_confidence, 4)
            if max_confidence is not None
            else None,
            "detections": detections,
            "image_url": image_url,
            "message": "Prediction complete",
        }

    except HTTPException:
        raise
    except Exception as e:
        print("DEBUG predict error:", str(e))
        raise HTTPException(status_code=500, detail=f"Inference failed: {str(e)}")



def generate_otp():
    return str(random.randint(100000, 999999))





def send_otp_email(to_email: str, otp: str):
    api_key = os.getenv("RESEND_API_KEY")

    print("DEBUG RESEND KEY:", "SET" if api_key else "NOT SET")

    try:
        response = requests.post(
            "https://api.resend.com/emails",
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json",
            },
            json={
                "from": "SporeX <onboarding@resend.dev>",
                "to": [to_email],
                "subject": "SporeX Email Verification",
                "html": f"<p>Your verification code is: <strong>{otp}</strong></p>",
            },
        )

        print("Email response:", response.text)

    except Exception as e:
        print("❌ Email error:", e)
# ----------------------------
# Settings ENDPOINTS
# enabling darkmode, profile delete access, log out , navigate to device page
# ----------------------------

# Optional: get scan history for a user
@app.get("/api/scans/{email}")
async def get_user_scans(email: str):
    scans = list(
        scans_col.find({"user_email": email}, {"_id": 0}).sort("created_at", -1)
    )
    return scans
