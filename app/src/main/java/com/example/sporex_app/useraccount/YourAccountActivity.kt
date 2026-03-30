package com.example.sporex_app.useraccount


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sporex_app.ui.theme.SPOREX_AppTheme


class YourAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkMode = com.example.sporex_app.utils.isDarkMode(this)

        setContent {
            SPOREX_AppTheme(darkTheme = darkMode) {
                YourAccountScreen()
            }
        }
    }
}