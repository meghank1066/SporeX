package com.example.sporex_app

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import org.junit.Rule
import org.junit.Test

class BottomNavBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bottomNav_hasAllTabs_andTheyAreClickable() {
        composeRule.setContent {
            SPOREX_AppTheme {
                BottomNavBar(currentScreen = "home")
            }
        }

        // contentDescription matches the strings in BottomNavBar.kt
        composeRule.onNodeWithContentDescription("Home").assertExists().assertHasClickAction()
        composeRule.onNodeWithContentDescription("Devices").assertExists().assertHasClickAction()
        composeRule.onNodeWithContentDescription("Camera").assertExists().assertHasClickAction()
        composeRule.onNodeWithContentDescription("Community").assertExists().assertHasClickAction()
        composeRule.onNodeWithContentDescription("Profile").assertExists().assertHasClickAction()
    }
}
