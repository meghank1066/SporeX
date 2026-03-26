package com.example.sporex_app.ui.components


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                bottomBar = { BottomNavBar(currentScreen = "camera") },
                containerColor = Color.White
            ) { padding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF06A546))
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
                                    .wrapContentHeight(),
                                color = Color.White,
                                shape = RoundedCornerShape(24.dp),
                                tonalElevation = 4.dp
                            ) {
                                MoldResultScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoldResultScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mold Detected Alert
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFE0B2), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFD32F2F)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Mold Detected", style = MaterialTheme.typography.titleMedium)
                Text("Cladosporium – estimated 65% likelihood", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Suggested Remedies
        Text("Suggested Remedies", style = MaterialTheme.typography.titleLarge)

        RemedyCard(
            title = "Mold Remover Spray",
            description = "A bleach-free spray that effectively removes mold and mildew.",
            onClick = { Toast.makeText(null, "Viewing Spray Details", Toast.LENGTH_SHORT).show() }
        )

        RemedyCard(
            title = "Mold Removal Service",
            description = "Professional mold removal services for thorough cleaning.",
            onClick = { Toast.makeText(null, "Viewing Service Details", Toast.LENGTH_SHORT).show() }
        )


    }
}

@Composable
fun RemedyCard(title: String, description: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(description, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06A546),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Details")
            }
        }
    }
}


