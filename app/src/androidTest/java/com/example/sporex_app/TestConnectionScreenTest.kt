package com.example.sporex_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sporex_app.ui.device.TestConnectionScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TestConnectionScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun testConnectionScreen_displaysCorrectDeviceInfo() {
        val testDevice = "SporeX-Scanner-01"
        val testFirmware = "1.0.5"

        composeRule.setContent {
            TestConnectionScreen(
                deviceName = testDevice,
                firmwareVersion = testFirmware,
                connectionStatus = true
            )
        }

        // Verify the device name is shown in the header and the details list
        composeRule.onNodeWithText(testDevice).assertIsDisplayed()
        composeRule.onNodeWithText("Device Name: $testDevice").assertIsDisplayed()
        composeRule.onNodeWithText("Firmware Version: $testFirmware").assertIsDisplayed()
    }

    @Test
    fun testConnectionScreen_showsConnectedStatus() {
        composeRule.setContent {
            TestConnectionScreen(
                deviceName = "Test Device",
                connectionStatus = true
            )
        }

        // Checks for the specific emoji and text defined in the Composable
        composeRule.onNodeWithText("Connection Status: Connected ✅").assertIsDisplayed()
        composeRule.onNodeWithText("Online ✅").assertIsDisplayed()
    }

    @Test
    fun testConnectionScreen_showsDisconnectedStatus() {
        composeRule.setContent {
            TestConnectionScreen(
                deviceName = "Test Device",
                connectionStatus = false
            )
        }

        // Checks for the disconnected state emoji
        composeRule.onNodeWithText("Connection Status: Disconnected ❌").assertIsDisplayed()
    }

    @Test
    fun reconnectButton_triggersCallback() {
        var reconnectClicked = false

        composeRule.setContent {
            TestConnectionScreen(
                deviceName = "Test Device",
                onReconnectClick = { reconnectClicked = true }
            )
        }

        // Find the button by its text and perform click
        composeRule.onNodeWithText("Reconnect Device").performClick()

        // Verify the callback was triggered
        assert(reconnectClicked)
    }
}