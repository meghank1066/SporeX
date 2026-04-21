package com.example.sporex_app.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.sporex_app.network.ReadingResponse
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar

@Composable
fun DeviceDashboardScreen(
    deviceName: String,
    onManageDeviceClick: () -> Unit,
    onCreateDeviceClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    var reading by remember { mutableStateOf<ReadingResponse?>(null) }

    LaunchedEffect(Unit) {
        try {
            reading = RetrofitClient.api.getLatestReading()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "device") },
        containerColor = MaterialTheme.colorScheme.primary,

        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateDeviceClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(56.dp)
            ) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Text(
                        text = deviceName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Online",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(16.dp))

                    Card(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Basic Readings",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand",
                                    tint = Color.White
                                )
                            }

                            if (expanded) {
                                Spacer(Modifier.height(12.dp))
                                StatRow(
                                    label = "Air Quality",
                                    value = getAirQuality(reading?.co2 ?: 0.0),
                                    desc = getAirQualityDesc(reading?.co2 ?: 0.0)
                                )

                                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

                                StatRow(
                                    label = "Humidity",
                                    value = "${reading?.humidity?.toInt() ?: "--"}%"
                                )
                                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                                StatRow(
                                    label = "CO₂",
                                    value = "${reading?.co2?.toInt() ?: "--"} ppm"
                                )
                                Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
                                StatRow(
                                    label = "% Chance of Mould",
                                    value = getMouldRisk(reading?.humidity ?: 0.0)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onManageDeviceClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Manage Device")
                    }
                }
            }
        }
    }
}

fun getAirQuality(co2: Double): String {
    return when {
        co2 < 800 -> "Good"
        co2 < 1200 -> "Moderate"
        else -> "Poor"
    }
}

fun getAirQualityDesc(co2: Double): String {
    return when {
        co2 < 800 -> "Good"
        co2 < 1200 -> "Okay"
        else -> "Bad"
    }
}

fun getMouldRisk(humidity: Double): String {
    return when {
        humidity < 60 -> "Low"
        humidity < 75 -> "Moderate"
        else -> "High"
    }
}
@Composable
fun StatRow(label: String, value: String, desc: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.secondary
            )
            desc?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
