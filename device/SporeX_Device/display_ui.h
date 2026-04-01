#pragma once
#include <Arduino.h>
#include "sensors.h"

bool displayInit();
void displayBoot(const char* line1, const char* line2 = nullptr);
void displayReadings(const Reading& r);
void displayError(const char* msg);
