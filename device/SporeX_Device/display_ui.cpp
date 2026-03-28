#include "display_ui.h"
#include "config.h"

#include <SPI.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SH110X.h>

static Adafruit_SH1107 display(
  SCREEN_WIDTH,
  SCREEN_HEIGHT,
  &SPI,
  OLED_DC,
  OLED_RST,
  OLED_CS
);

bool displayInit() {

  SPI.begin(OLED_CLK, -1, OLED_MOSI, OLED_CS);

  if (!display.begin()) {
    Serial.println("SH1107 init failed");
    return false;
  }

  display.setRotation(0); 
  display.clearDisplay();
  display.display();
  return true;
}

void displayBoot(const char* line1, const char* line2) {
  display.clearDisplay();
  display.setTextColor(SH110X_WHITE);
  display.setTextSize(2);
  display.setCursor(0, 10);
  display.println(line1);

  if (line2) {
    display.setTextSize(1);
    display.setCursor(0, 45);
    display.println(line2);
  }
  display.display();
}

void displayReadings(const Reading& r) {
  display.clearDisplay();
  display.setTextColor(SH110X_WHITE);

  // TEMP
  display.setTextSize(2);
  display.setCursor(0, 0);
  display.print("Temp");

  display.setTextSize(3);
  display.setCursor(0, 22);
  display.print(r.tempC, 1);

  display.setTextSize(2);
  display.print((char)247); // degree symbol
  display.print("C");

  // RH
  display.setTextSize(2);
  display.setCursor(0, 62);
  display.print("RH: ");
  display.print(r.rh, 1);
  display.print("%");

  // CO2
  display.setTextSize(2);
  display.setCursor(0, 92);
  display.print("CO2: ");
  display.print(r.co2);
  display.print(" ppm");

  display.display();
}

void displayError(const char* msg) {
  display.clearDisplay();
  display.setTextColor(SH110X_WHITE);
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.println("ERROR:");
  display.println(msg);
  display.display();
}
