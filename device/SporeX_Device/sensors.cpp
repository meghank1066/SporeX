#include "sensors.h"
#include "config.h"

#include <Wire.h>
#include <SensirionI2cScd4x.h>

#ifdef NO_ERROR
#undef NO_ERROR
#endif
#define NO_ERROR 0

static SensirionI2cScd4x sensor;
static char errorMessage[64];
static int16_t error;

static void printUint64(uint64_t value) {
  Serial.print("0x");
  Serial.print((uint32_t)(value >> 32), HEX);
  Serial.print((uint32_t)(value & 0xFFFFFFFF), HEX);
}

bool sensorsInit() {
  Wire.begin(I2C_SDA, I2C_SCL);
  Wire.setClock(100000);

  sensor.begin(Wire, SCD41_I2C_ADDR_62);

  delay(30);

  error = sensor.wakeUp();
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("wakeUp() error: "); Serial.println(errorMessage);
  }

  error = sensor.stopPeriodicMeasurement();
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("stopPeriodicMeasurement() error: "); Serial.println(errorMessage);
  }

  error = sensor.reinit();
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("reinit() error: "); Serial.println(errorMessage);
  }

  uint64_t serialNumber = 0;
  error = sensor.getSerialNumber(serialNumber);
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("getSerialNumber() error: "); Serial.println(errorMessage);
    return false;
  }

  Serial.print("SCD41 serial number: ");
  printUint64(serialNumber);
  Serial.println();

  error = sensor.startPeriodicMeasurement();
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("startPeriodicMeasurement() error: "); Serial.println(errorMessage);
    return false;
  }

  delay(15000);
  return true;
}

bool sensorsRead(Reading &out) {
  bool dataReady = false;

  error = sensor.getDataReadyStatus(dataReady);
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("getDataReadyStatus() error: "); Serial.println(errorMessage);
    return false;
  }

  if (!dataReady) return false;

  uint16_t co2 = 0;
  float tempC = 0.0f;
  float rh = 0.0f;

  error = sensor.readMeasurement(co2, tempC, rh);
  if (error != NO_ERROR) {
    errorToString(error, errorMessage, sizeof errorMessage);
    Serial.print("readMeasurement() error: "); Serial.println(errorMessage);
    return false;
  }

  out.co2 = co2;
  out.tempC = tempC;
  out.rh = rh;

  return true;
}
