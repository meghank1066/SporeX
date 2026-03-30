package com.example.sporex_app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sporex_app.ui.community.CreatePostScreen
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import org.junit.Rule
import org.junit.Test

class CreatePostScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun createPost_initialState_buttonIsDisabled() {
        composeRule.setContent {
            SPOREX_AppTheme {
                CreatePostScreen()
            }
        }

        // Verify the header is there
        composeRule.onNodeWithText("Create Post").assertIsDisplayed()

        // The button should be disabled because postContent is initially empty ("")
        composeRule.onNodeWithText("Post").assertIsNotEnabled()
    }

    @Test
    fun createPost_enteringText_enablesButton() {
        composeRule.setContent {
            SPOREX_AppTheme {
                CreatePostScreen()
            }
        }

        // Find the text field by its placeholder
        val textField = composeRule.onNodeWithText("Share your mold experience or ask for advice...")

        // Type some content
        textField.performTextInput("This is a test post about mold.")

        // Now the button should be enabled
        composeRule.onNodeWithText("Post").assertIsEnabled()
    }

//    @Test
//    fun createPost_clearText_disablesButtonAgain() {
//        composeRule.setContent {
//            SPOREX_AppTheme {
//                CreatePostScreen()
//            }
//        }
//
//        val textField = composeRule.onNodeWithText("Share your mold experience or ask for advice...")
//
//        // Type then clear
//        textField.performTextInput("Temporary text")
//        textField.performTextInput("") // Clear it
//
//        // Button should go back to disabled
//        composeRule.onNodeWithText("Post").assertIsNotEnabled()
//    }

    @Test
    fun createPost_contentDescription_isPresent() {
        // This checks if the card layout exists by looking for the specific prompt text
        composeRule.setContent {
            SPOREX_AppTheme {
                CreatePostScreen()
            }
        }

        composeRule.onNodeWithText("What's on your mind?").assertExists()
    }
}