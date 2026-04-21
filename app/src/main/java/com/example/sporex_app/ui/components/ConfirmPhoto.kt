package com.example.sporex_app.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.utils.uriToMultipart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
                ) { padding ->

                    val bottomPadding = padding.calculateBottomPadding()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(bottom = bottomPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TopBar()

                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                            ) {
                                ConfirmationScreen(
                                    imageUri = imageUri,
                                    onBack = { activity.finish() },
                                    onConfirm = { uriString, setLoading ->
                                        if (uriString.isBlank()) {
                                            Toast.makeText(
                                                activity,
                                                "No image selected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            setLoading(false)
                                        } else {
                                            activity.lifecycleScope.launch {
                                                try {
                                                    val uri = Uri.parse(uriString)
                                                    val filePart = uriToMultipart(activity, uri)

                                                    val userEmail = activity
                                                        .getSharedPreferences("auth", Context.MODE_PRIVATE)
                                                        .getString("user_email", "")
                                                        .orEmpty()

                                                    val emailPart = if (userEmail.isNotBlank()) {
                                                        userEmail.toRequestBody("text/plain".toMediaTypeOrNull())
                                                    } else {
                                                        null
                                                    }

                                                    val response = RetrofitClient.api.predictImage(
                                                        file = filePart,
                                                        email = emailPart
                                                    )

                                                    if (response.isSuccessful && response.body() != null) {
                                                        val result = response.body()!!

                                                        val intent = Intent(
                                                            activity,
                                                            ResultActivity::class.java
                                                        ).apply {
                                                            putExtra("mould_detected", result.mould_detected)
                                                            putExtra("max_confidence", result.max_confidence ?: 0.0)
                                                            putExtra("image_url", result.image_url ?: "")
                                                            putExtra("message", result.message)
                                                        }
                                                        activity.startActivity(intent)
                                                    } else {
                                                        setLoading(false)
                                                        Toast.makeText(
                                                            activity,
                                                            "Prediction failed: ${response.code()}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                } catch (e: Exception) {
                                                    setLoading(false)
                                                    Toast.makeText(
                                                        activity,
                                                        "Error: ${e.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                )
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
        onBack: () -> Unit,
        onConfirm: (String, (Boolean) -> Unit) -> Unit
    ) {
        val uri = imageUri?.let { Uri.parse(it) }
        var isLoading by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Is this the correct photo for analysis?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Analyzing image...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBack,
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Retake")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (imageUri != null && !isLoading) {
                            isLoading = true
                            onConfirm(imageUri) { loading ->
                                isLoading = loading
                            }
                        }
                    },
                    enabled = imageUri != null && !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text(if (isLoading) "Analyzing..." else "Confirm")
                }
            }
        }
    }
}