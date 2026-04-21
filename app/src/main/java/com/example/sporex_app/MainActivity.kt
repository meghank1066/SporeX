package com.example.sporex_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.sporex_app.ui.components.UploadActivity
import com.example.sporex_app.ui.components.ProductsActivity
import com.example.sporex_app.ui.components.HistoryActivity
import com.example.sporex_app.ui.screens.HomeScreen
import com.example.sporex_app.utils.isDarkMode
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ScaffoldDefaults.contentWindowInsets
import com.example.sporex_app.utils.setDarkMode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)


        setContent {
            val context = LocalContext.current
            var isDarkMode by remember { mutableStateOf(isDarkMode(context)) } // ← read from prefs

            SPOREX_AppTheme(darkTheme = isDarkMode) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = { BottomNavBar(currentScreen = "home") },
                    contentWindowInsets = WindowInsets.safeDrawing // 👈 THIS LINE FIXES IT
                ) { innerPadding -> // 1. Name the padding values here
                    HomeScreen(
                        // 2. Apply the padding to the modifier
                        modifier = Modifier.padding(innerPadding),
                        onUploadClick = {
                            context.startActivity(Intent(context, UploadActivity::class.java))
                        },
                        onProductsClick = {
                            context.startActivity(Intent(context, ProductsActivity::class.java))
                        },
                        onHistoryClick = {
                            context.startActivity(Intent(context, HistoryActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}
