package com.example.sporex_app.ui.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode

class ConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUri = intent.getStringExtra("imageUri")
        val activity = this@ConfirmationActivity

        setContent {
            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {
                Scaffold(
                    bottomBar = { BottomNavBar(currentScreen = "camera") },
                    // Use theme color instead of Color.White
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) { padding ->

                    val bottomPadding = padding.calculateBottomPadding()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            // Use primary color from theme
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(bottom = bottomPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TopBar()

                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                // Use surface color from theme
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                            ) {
                                ConfirmationScreen(
                                    imageUri = imageUri,
                                    onNext = {
                                        activity.startActivity(
                                            Intent(activity, ResultActivity::class.java)
                                        )
                                    },
                                    onBack = { activity.finish() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationScreen(
    imageUri: String?,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val uri = imageUri?.let { Uri.parse(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            text = "Review Your Photo",
            style = MaterialTheme.typography.headlineSmall,
            // Use onSurface so it flips between black/white automatically
            color = MaterialTheme.colorScheme.surface
        )

        Spacer(Modifier.height(20.dp))

        uri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Uploaded mould photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Is this the correct photo for analysis?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.surface
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Retake Button (Secondary style)
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    // Use secondary or surface variant for a "lesser" action
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Retake")
            }

            Spacer(Modifier.width(16.dp))
            Button(
                onClick = onNext,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Confirm")
            }
        }
    }
}