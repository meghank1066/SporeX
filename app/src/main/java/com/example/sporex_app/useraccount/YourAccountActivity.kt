package com.example.sporex_app.useraccount


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.sporex_app.ui.theme.SPOREX_AppTheme


class YourAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SPOREX_AppTheme {
                YourAccountScreen()
            }
        }
    }
}