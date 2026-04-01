#pragma once
#include "sensors.h"

bool netInit();
void netLoop();
bool netSend(const Reading& r);
