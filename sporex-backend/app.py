import os

from dotenv import load_dotenv
from fastapi import FastAPI, Header, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel, EmailStr
from pymongo import MongoClient
from passlib.context import CryptContext
from datetime import datetime, timezone
import random
from datetime import timedelta
from pathlib import Path
from dotenv import load_dotenv

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
load_dotenv()

MONGO_URI = os.getenv("MONGODB_URI")
DB_NAME = os.getenv("MONGODB_DB_NAME")
DEVICE_INGEST_TOKEN = os.getenv("DEVICE_INGEST_TOKEN")

if not MONGO_URI or not DB_NAME:
    raise RuntimeError("Missing MONGODB_URI or MONGODB_DB_NAME in environment")

client = MongoClient(MONGO_URI)
db = client[DB_NAME]
users_col = db["users"]
readings_col = db["sensor_readings"]

# ✅ NEW: products collection
products_col = db["products"]

# ✅ NEW: posts collection
posts_col = db["posts"]

app = FastAPI(
    title="SporeX Backend",
    version="0.1.0",
)

# CORS – Android isn’t a browser, but this also helps if you later add a web UI
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],        # later you can lock this down
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

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
    ts: int | None = None  # optional epoch seconds from device

# ✅ NEW: product models
class ProductDto(BaseModel):
    id: str
    name: str
    sustainable: bool = True
    best_for: str
    steps: list[str]
    warning: str | None = None

# ✅ NEW: post models
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

    

# ---------- Routes ----------
@app.get("/api/health", response_model=BasicResponse)
async def health_check():
    return {"success": True, "message": "Backend running"}

@app.post("/api/register")
async def register(body: RegisterBody):
    # 1) Check if a user with this email already exists
    if users_col.find_one({"email": body.email}):
        return JSONResponse(
            status_code=409,
            content={"success": False, "message": "User already exists"},
        )

    # 2) Decide username (either from body or from email prefix)
    username = body.username or body.email.split("@")[0]
     otp = generate_otp()
    # 3) Hash the password
    password_hash = hash_password(body.password)

    # 4) Build document
    user_doc = {
        "email": body.email,
        "username": username,
        "password_hash": password_hash,
        "role": "member",
        "status": "active",
        "is_verified": False,
        "created_at": datetime.now(timezone.utc),

        "settings": {
        "dark_mode": False,
        "notifications_enabled": True,
        "data_personalisation": True,
        "otp": otp,
        "otp_expiry": datetime.now(timezone.utc) + timedelta(minutes=10),
        "app_customisation": {
            "accent_color": "green",
            "layout_style": "default"
        
           }
    }

}


    if body.name:
        user_doc["name"] = body.name

    users_col.insert_one(user_doc)
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



# keeping already existing users
    if not user.get("is_verified", True):
    return JSONResponse(
        status_code=403,
        content={"success": False, "message": "Please verify your email"}
    )
    # (Optional) return basic user info for the app
    return {
        "success": True,
        "message": "Login OK",
        "user": {
            "email": user.get("email"),
            "username": user.get("username"),
            "name": user.get("name"),
        }
    }

@app.post("/api/readings", response_model=BasicResponse)
async def ingest_reading(
    body: ReadingBody,
    x_device_token: str | None = Header(default=None),
):
    if not DEVICE_INGEST_TOKEN or x_device_token != DEVICE_INGEST_TOKEN:
        raise HTTPException(status_code=401, detail="Unauthorized device")

    # ✅ safer ts handling
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

# ----------------------------
#  PRODUCTS ENDPOINTS
# ----------------------------

@app.get("/api/products")
async def list_products():
    products = list(products_col.find({}, {"_id": 0}))
    return products

@app.get("/api/products/{product_id}")
def get_product(product_id: str):
    product = products_col.find_one(
        {"id": product_id},
        {"_id": 0}
    )
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product

# Posts endpoints

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
    """Get all posts"""
    posts = list(posts_col.find({}, {"_id": 0}))
    return posts

@app.get("/api/posts/{post_id}")
async def get_post(post_id: str):
    """Get a specific post by ID"""
    from bson import ObjectId
    try:
        post = posts_col.find_one(
            {"_id": ObjectId(post_id)},
            {"_id": 0}
        )
    except:
        raise HTTPException(status_code=400, detail="Invalid post ID")
    
    if not post:
        raise HTTPException(status_code=404, detail="Post not found")
    return post

@app.post("/api/posts/{post_id}/replies", response_model=BasicResponse)
async def add_reply(post_id: str, body: ReplyCreateBody):
    """Add a reply to a post"""
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
    
    return {
        "success": True,
        "message": "Reply added to post"
    }



def generate_otp():
    return str(random.randint(100000, 999999))
# ----------------------------
# Settings ENDPOINTS
# enabling darkmode, profile delete access, log out , navigate to device page
# ----------------------------

