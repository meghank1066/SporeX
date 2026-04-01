#include <Arduino.h>

#include "config.h"
#include "sensors.h"
#include "display_ui.h"
#include "net_client.h"

static uint32_t lastSensorMs = 0;
static uint32_t lastSendMs = 0;
static Reading latest;

void setup() {
  Serial.begin(115200);
  delay(200);

  if (!displayInit()) {
    Serial.println("OLED failed");
    while (1) delay(100);
  }
  displayBoot("Booting...", "Init OLED OK");

  if (!sensorsInit()) {
    displayError("SCD41 init failed");
    while (1) delay(100);
  }
  displayBoot("SCD41 OK", "Starting...");

  netInit();
}

void loop() {
  netLoop();

  uint32_t now = millis();

  // Read + display when fresh data is available
  if (now - lastSensorMs >= SENSOR_INTERVAL_MS) {
    lastSensorMs = now;

    if (sensorsRead(latest)) {
      Serial.print("CO2 [ppm]: "); Serial.println(latest.co2);
      Serial.print("Temp [C]: "); Serial.println(latest.tempC);
      Serial.print("RH [%]: "); Serial.println(latest.rh);
      Serial.println("---");

      displayReadings(latest);
    } else {
      Serial.println("Data not ready yet...");
    }
  }

  // Send less frequently (database / backend)
  if (now - lastSendMs >= SEND_INTERVAL_MS) {
    lastSendMs = now;
    netSend(latest);
  }
}
