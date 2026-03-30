package com.example.sporex_app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sporex_app.ui.device.CreateDeviceScreen
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CreateDeviceScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun addDevice_passesSelectedDevice_toCallback() {
        var captured = ""

        composeRule.setContent {
            SPOREX_AppTheme {
                CreateDeviceScreen(onCreateClick = { captured = it })
            }
        }

        // Click the scan button
        composeRule.onNodeWithText("Scan for Devices").assertExists().performClick()

        // Wait for the device list to appear and click on a device
        composeRule.onNodeWithText("Sporex Sensor A").assertExists().performClick()

        assertEquals("Sporex Sensor A", captured)
    }
}
