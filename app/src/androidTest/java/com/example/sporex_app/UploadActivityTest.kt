package com.example.sporex_app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sporex_app.ui.components.UploadActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UploadActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<UploadActivity>()

    @Test
    fun initialState_showsSelectPhoto_andNoNextButton() {
        composeRule.onNodeWithText("Tap to upload a photo").assertExists()
        composeRule.onNodeWithText("Next").assertDoesNotExist()
        composeRule.onNodeWithText("← Back").assertExists()
    }

    @Test
    fun clickingBack_finishesActivity() {
        composeRule.onNodeWithText("← Back").performClick()
        composeRule.waitForIdle()
        assertTrue(composeRule.activity.isFinishing)
    }
}
