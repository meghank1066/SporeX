package com.example.sporex_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.content.Context
import com.example.sporex_app.network.ScanHistoryDto
import com.example.sporex_app.network.ScanResponse
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.onboarding.OnboardingOverlay
import com.example.sporex_app.ui.onboarding.OnboardingPageOne
import com.example.sporex_app.ui.onboarding.OnboardingStep

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onUploadClick: () -> Unit,
    onProductsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {

    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPageOne(context) }

    val onboardingSteps = listOf(
        OnboardingStep(
            title = "Welcome to SPOREX",
            description = "Your personal mold detection assistant. Let's get started!"
        ),
        OnboardingStep(
            title = "Scan Your Home",
            description = "Use the camera to scan for mold and get instant results."
        ),
        OnboardingStep(
            title = "Notifications",
            description = "Check your past scans and monitor your home's health over time."
        )
    )

    var stepIndex by remember { mutableStateOf(0) }

    var showOnboarding by rememberSaveable {
        mutableStateOf(onboardingPrefs.isFirstLaunch())
    }

    var scan by remember { mutableStateOf<ScanHistoryDto?>(null) }
    LaunchedEffect(Unit) {
        val userEmail = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""

        try {
            val response = RetrofitClient.api.getUserScans(userEmail)

            if (response.isSuccessful) {
                scan = response.body()
                    .orEmpty()
                    .maxByOrNull { it.created_at ?: "" }

                println("HOME LATEST SCAN: $scan")
            } else {
                println("HOME SCAN ERROR CODE: ${response.code()}")
            }
        } catch (e: Exception) {
            println("HOME SCAN ERROR: ${e.message}")
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = { TopBar() },
        containerColor = MaterialTheme.colorScheme.primary
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Welcome Back!",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                PreviousCaseCard(
                    scan = scan,
                    onClick = onHistoryClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Scan For Mould",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                CameraCard(onUploadClick = onUploadClick)
            }

            if (showOnboarding) {
                OnboardingOverlay(
                    step = onboardingSteps[stepIndex],
                    onNext = {
                        if (stepIndex < onboardingSteps.lastIndex) {
                            stepIndex++
                        } else {
                            onboardingPrefs.finishOnboarding()
                            showOnboarding = false
                        }
                    },
                    onSkip = {
                        onboardingPrefs.finishOnboarding()
                        showOnboarding = false
                    }
                )
            }
        }
    }
}
@Composable
private fun CameraCard(onUploadClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onUploadClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Camera",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(140.dp)
            )
        }
    }
}

@Composable
private fun PreviousCaseCard(
    scan: ScanHistoryDto?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {


                Text(
                    text = "Previous Case",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = scan?.max_confidence?.let {
                        "${(it * 100).toInt()}%"
                    } ?: "--%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )



                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Click for more information",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF06A546), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "View Case",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}