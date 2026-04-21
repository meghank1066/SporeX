package com.example.sporex_app.ui.components

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mouldDetected = intent.getBooleanExtra("mould_detected", false)
        val maxConfidence = intent.getDoubleExtra("max_confidence", 0.0)
        val imageUrl = intent.getStringExtra("image_url").orEmpty()
        val message = intent.getStringExtra("message").orEmpty()

        setContent {
            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {
                Scaffold(
                    bottomBar = { BottomNavBar(currentScreen = "camera") },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) { padding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(bottom = padding.calculateBottomPadding())
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TopBar()

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 24.dp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    //  .wrapContentHeight() < merge conflict resolve, look here if the page looks off
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(24.dp),
                                    tonalElevation = 4.dp
                                ) {
                                    MoldResultScreen(
                                        mouldDetected = mouldDetected,
                                        maxConfidence = maxConfidence,
                                        imageUrl = imageUrl,
                                        message = message
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
    fun MoldResultScreen(
        mouldDetected: Boolean,
        maxConfidence: Double,
        imageUrl: String,
        message: String
    ) {
        val context = LocalContext.current

        // For emulator testing. If using Render, imageUrl is already relative to same host.
        val fullImageUrl = if (imageUrl.startsWith("http")) {
            imageUrl
        } else {
            "https://sporex.onrender.com$imageUrl"
        }

        val confidencePercent = (maxConfidence * 100).toInt()

        val resultTitle = if (mouldDetected) "Possible Mould Detected" else "No Mould Detected"

        val resultSubtitle = when {
            !mouldDetected -> "The model did not detect mould in this image."
            maxConfidence >= 0.7 -> "High confidence result: $confidencePercent% likelihood"
            maxConfidence >= 0.4 -> "Moderate confidence result: $confidencePercent% likelihood"
            else -> "Low confidence result: $confidencePercent% likelihood"
        }

        val adviceText = when {
            !mouldDetected -> "No mould was detected. You can try again with a clearer image if you are unsure."
            maxConfidence >= 0.7 -> "Mould is likely present. Inspect the area closely, improve ventilation, and consider treatment."
            maxConfidence >= 0.4 -> "Possible mould detected. Retake the image in better lighting and inspect the area."
            else -> "This result has low confidence. Try another photo from a closer angle with better lighting."
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (mouldDetected) Color(0xFFFFE0B2) else Color(0xFFE8F5E9), // conlfict, change if looks off
                        // color = MaterialTheme.colorScheme.errorContainer
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (mouldDetected) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = "Result icon",
                    tint = if (mouldDetected) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                    // conflict change if looks off
                    //MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = resultTitle,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = resultSubtitle,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (imageUrl.isNotBlank()) {
                Column {
                    Text(
                        text = "Analyzed Image",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Image(
                        painter = rememberAsyncImagePainter(fullImageUrl),
                        contentDescription = "Analyzed mould image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Analysis Summary",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message.ifBlank { "Prediction complete" },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Confidence: $confidencePercent%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            // Suggested Remedies
            Text(
                "Suggested Remedies", style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recommended Action",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = adviceText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, UploadActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Try Another Image")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, ProductsActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06A546),
                    contentColor = Color.Black
                )
            ) {
                Text("View More Remedies")
            }

        }
    }

    @Composable
    fun RemedyCard(title: String, description: String, onClick: () -> Unit) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    title, style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}
