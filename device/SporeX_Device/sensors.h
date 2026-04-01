#pragma once
#include <Arduino.h>

struct Reading {
  uint16_t co2 = 0;
  float tempC = 0.0f;
  float rh = 0.0f;
};

bool sensorsInit();
bool sensorsRead(Reading &out);   // returns true only when a fresh reading was read
