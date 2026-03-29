package com.example.sporex_app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.sporex_app.ui.components.MoldResultScreen
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import org.junit.Rule
import org.junit.Test

class MoldResultScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun moldDetected_isShown() {
        composeRule.setContent {
            SPOREX_AppTheme {
                MoldResultScreen()
            }
        }

        composeRule.onNodeWithText("Mold Detected").assertExists()
        composeRule.onNodeWithText("Cladosporium – estimated 65% likelihood").assertExists()
    }

    @Test
    fun clickingViewDetails_performsAction() {
        composeRule.setContent {
            SPOREX_AppTheme {
                MoldResultScreen()
            }
        }

        composeRule.onAllNodesWithText("View Details")[0].assertExists().performClick()
    }

    @Test
    fun askQuestionButton_exists() {
        composeRule.setContent {
            SPOREX_AppTheme {
                MoldResultScreen()
            }
        }

        composeRule.onNodeWithText("Ask Question").assertExists()
    }
}
