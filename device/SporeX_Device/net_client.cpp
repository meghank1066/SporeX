#include "net_client.h"
#include "config.h"

#include <Arduino.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <time.h>

// --------- NTP time sync ---------
static bool syncTimeWithNTP() {
  configTime(GMT_OFFSET_SEC, DAYLIGHT_OFFSET_SEC, NTP_SERVER);

  Serial.print("[NTP] Syncing time");
  time_t now = 0;
  int retries = 0;

  while (now < 1700000000 && retries < 20) {
    delay(500);
    Serial.print(".");
    time(&now);
    retries++;
  }
  Serial.println();

  if (now < 1700000000) {
    Serial.println("[NTP] Failed to sync time");
    return false;
  }

  Serial.print("[NTP] Time synced. Epoch: ");
  Serial.println((uint32_t)now);
  return true;
}

static bool wifiConnected() {
  return WiFi.status() == WL_CONNECTED;
}

// --------- Public API ---------
bool netInit() {
  WiFi.mode(WIFI_STA);
  WiFi.setSleep(false);

  Serial.print("[NET] Connecting to WiFi ");
  Serial.println(WIFI_SSID);

  WiFi.begin(WIFI_SSID, WIFI_PASS);

  uint32_t start = millis();
  while (WiFi.status() != WL_CONNECTED && (millis() - start) < 20000) {
    delay(500);
    Serial.print(".");
    Serial.print(WiFi.status());
  }
  Serial.println();

  if (!wifiConnected()) {
    Serial.println("[NET] WiFi connect failed (timeout)");
    return false;
  }

  Serial.print("[NET] WiFi connected. IP: ");
  Serial.println(WiFi.localIP());

  // NTP time (Option B)
  return syncTimeWithNTP();
}

void netLoop() {
  // Minimal reconnect (no spam)
  static uint32_t lastAttempt = 0;

  if (wifiConnected()) return;

  uint32_t now = millis();
  if (now - lastAttempt < 5000) return;
  lastAttempt = now;

  Serial.println("[NET] WiFi dropped, reconnecting...");
  WiFi.disconnect(true);
  delay(200);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
}

bool netSend(const Reading& r) {
  if (!wifiConnected()) {
    Serial.println("[NET] Not connected, skip send");
    return false;
  }

  // Ensure time is valid
  time_t now;
  time(&now);
  if (now < 1700000000) {
    Serial.println("[NTP] Time invalid, re-syncing...");
    if (!syncTimeWithNTP()) {
      Serial.println("[NET] Cannot send without valid time");
      return false;
    }
    time(&now);
  }

  const uint32_t ts = (uint32_t)now;

  // Build JSON matching your FastAPI ReadingBody
  String json;
  json.reserve(160);
  json += "{";
  json += "\"device_id\":\"" + String(DEVICE_ID) + "\",";
  json += "\"co2\":" + String(r.co2) + ",";
  json += "\"temp_c\":" + String(r.tempC, 2) + ",";
  json += "\"humidity\":" + String(r.rh, 2) + ",";
  json += "\"ts\":" + String(ts);
  json += "}";

  WiFiClient client;
  client.setTimeout(20000);

  HTTPClient http;
  http.setTimeout(20000);
  http.setReuse(false);

  Serial.print("[NET] POST ");
  Serial.println(API_URL);

  if (!http.begin(client, API_URL)) {
    Serial.println("[NET] http.begin failed");
    return false;
  }

  http.addHeader("Content-Type", "application/json");
  http.addHeader("X-Device-Token", DEVICE_INGEST_TOKEN);

  int httpCode = http.POST((uint8_t*)json.c_str(), json.length());
  Serial.print("[NET] HTTP ");
  Serial.println(httpCode);

  if (httpCode < 0) {
    Serial.print("[NET] HTTP error: ");
    Serial.println(http.errorToString(httpCode));
    http.end();
    return false;
  }

  String resp = http.getString();
  if (resp.length() > 0) {
    Serial.print("[NET] Response: ");
    Serial.println(resp);
  }

  http.end();
  return (httpCode >= 200 && httpCode < 300);
}
