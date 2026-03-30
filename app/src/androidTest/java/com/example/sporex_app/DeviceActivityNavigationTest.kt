package com.example.sporex_app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sporex_app.ui.device.DeviceActivity
import org.junit.Rule
import org.junit.Test

class DeviceActivityNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<DeviceActivity>()

    @Test
    fun clickingManageDevice_navigatesToEditDeviceScreen() {
        // Start destination is dashboard
        composeRule.onNodeWithText("Manage Device").assertExists().performClick()

        // We should now be on Edit screen
        composeRule.onNodeWithText("Remove Device").assertExists()
    }
}
