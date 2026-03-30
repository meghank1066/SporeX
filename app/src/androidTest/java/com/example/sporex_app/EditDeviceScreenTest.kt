package com.example.sporex_app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sporex_app.ui.device.EditDeviceScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EditDeviceScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun editDevice_showsCurrentDeviceNameAndOnlineStatus() {
        val testName = "AIREX 400"
        composeRule.setContent {
            EditDeviceScreen(
                deviceName = testName,
                onRename = {},
                onBackClick = {},
                onTestConnectionClick = {}
            )
        }

        // Verify the device name passed in is displayed
        composeRule.onNodeWithText(testName).assertIsDisplayed()
        // Verify the status text is displayed
        composeRule.onNodeWithText("Online").assertIsDisplayed()
    }

//    @Test
//    fun editDevice_opensRenameDialog_andCallsCallback() {
//        var capturedNewName = "New Name"
//        val initialName = "Old Name"
//
//        composeRule.setContent {
//            EditDeviceScreen(
//                deviceName = initialName,
//                onRename = { capturedNewName = it },
//                onBackClick = {},
//                onTestConnectionClick = {}
//            )
//        }
//
//        // 1. Click the "Edit Device Name" setting item
//        composeRule.onNodeWithText("Edit Device Name").performClick()
//
//        // 2. Verify dialog appears (it has a title "Edit Device Name")
//        // Note: There are two nodes with this text now (the item and the dialog title)
//        // We look for the one inside the dialog/popup specifically or use useUnmergedTree
//        composeRule.onNodeWithText("Save").assertIsDisplayed()
//
//        // 3. Change text and click Save
//        val newName = "New Scanner"
//        // Find the text field by the current name it contains
//        composeRule.onNodeWithText(initialName).performTextReplacement(newName)
//        composeRule.onNodeWithText("Save").performClick()
//
//        // 4. Verify the callback was triggered with the correct string
//        assertEquals(newName, capturedNewName)
//    }

    @Test
    fun testConnection_triggersCallback() {
        var testClicked = false
        composeRule.setContent {
            EditDeviceScreen(
                deviceName = "Test Device",
                onRename = {},
                onBackClick = {},
                onTestConnectionClick = { testClicked = true }
            )
        }

        // Click the Test Connection item
        composeRule.onNodeWithText("Test Connection").performClick()

        // Verify callback
        assert(testClicked)
    }

    @Test
    fun actionButtons_areDisplayedWithCorrectText() {
        composeRule.setContent {
            EditDeviceScreen(
                deviceName = "Test Device",
                onRename = {},
                onBackClick = {},
                onTestConnectionClick = {}
            )
        }

        // Verify the two bottom action buttons exist
        composeRule.onNodeWithText("Reset Device").assertExists().assertHasClickAction()
        composeRule.onNodeWithText("Remove Device").assertExists().assertHasClickAction()
    }
}