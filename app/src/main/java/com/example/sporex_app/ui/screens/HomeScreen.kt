package com.example.sporex_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sporex_app.ui.navigation.TopBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onUploadClick: () -> Unit,
    onProductsClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
//            .background(Color(0xFF06A546))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopBar()
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
//                color = Color(0xFF06A546),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 12.dp)
                ) {
//                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Welcome Back!",
//                        color = Color.White,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 20.dp)
                    )

                    Spacer(Modifier.height(20.dp))

                    PreviousCaseCard(onClick = onHistoryClick)

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Scan For Mould",
//                        color = Color.White,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(start = 20.dp)
                    )

                    CameraCard(onUploadClick = onUploadClick)

                    Button(
                        onClick = onProductsClick,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color.White,
//                            contentColor = Color.Black
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Products & Methods")
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CameraCard(onUploadClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onUploadClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
//            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Camera",
//                    tint = Color.Black,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
@Composable
private fun PreviousCaseCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
//        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    text = "65%",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "SPOREX has detected 65% exposure of Trichoderma in your home.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Click for more information",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
//                    color = Color(0xFF06A546),
//                    style = MaterialTheme.typography.bodySmall
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
//                    tint = Color.White
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
