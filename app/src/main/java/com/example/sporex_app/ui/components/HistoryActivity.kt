package com.example.sporex_app.ui.components

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import com.example.sporex_app.network.ScanHistoryDto
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

class HistoryActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {
                HistoryScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val userEmail = remember {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""
    }

    var scans by remember { mutableStateOf<List<ScanHistoryDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(userEmail, reloadKey) {
        if (userEmail.isBlank()) {
            error = "No logged in user found"
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        error = null

        try {
            val response = RetrofitClient.api.getUserScans(userEmail)
            if (response.isSuccessful) {
                scans = response.body().orEmpty()
            } else {
                error = "Failed to load history (${response.code()})"
            }
        } catch (e: Exception) {
            error = "Error loading history: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }
    val recentScans = scans.filter { isWithinLastDays(it.created_at, 7) }
    val olderScans = scans.filter { !isWithinLastDays(it.created_at, 7) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "history") },
        containerColor = MaterialTheme.colorScheme.primary
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                error != null -> {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                scans.isEmpty() -> {
                    Text(
                        text = "No scan history yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                else -> {
                    Text(
                        text = "Last 7 Days",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(recentScans) { scan ->
                            ScanCard(
                                scan = scan,
                                onDelete = { scanId ->
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.api.deleteScan(scanId)
                                            if (response.isSuccessful) {
                                                reloadKey++
                                            } else {
                                                error = "Failed to delete case (${response.code()})"
                                            }
                                        } catch (e: Exception) {
                                            error = "Error deleting case: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Text(
                        text = "Older",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(olderScans) { scan ->
                            ScanCard(
                                scan = scan,
                                onDelete = { scanId ->
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.api.deleteScan(scanId)
                                            if (response.isSuccessful) {
                                                reloadKey++
                                            } else {
                                                error = "Failed to delete case (${response.code()})"
                                            }
                                        } catch (e: Exception) {
                                            error = "Error deleting case: ${e.localizedMessage}"
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
fun ScanCard(
    scan: ScanHistoryDto,
    onDelete: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val confidencePercent = ((scan.max_confidence ?: 0.0) * 100).toInt()

    val resultText = if (scan.mould_detected) {
        "OPEN CASE"
    } else {
        "NO MOULD DETECTED"
    }

    val chanceText = if (scan.mould_detected) {
        "$confidencePercent% confidence of mould detected"
    } else {
        "No mould detected in this scan"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatScanDate(scan.created_at),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = resultText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chanceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Case",
                        tint = Color.Red
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "View Case",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Case") },
            text = { Text("Are you sure you want to delete this case?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(scan.id)
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
private fun parseScanDate(dateString: String?): Date? {
    if (dateString.isNullOrBlank()) return null

    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    for (pattern in formats) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.isLenient = false
            val parsed = sdf.parse(dateString)
            if (parsed != null) return parsed
        } catch (_: Exception) {
        }
    }

    return null
}

fun formatScanDate(dateString: String?): String {
    return try {
        val parsedDate = parseScanDate(dateString) ?: return "Unknown date"
        val output = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        output.format(parsedDate)
    } catch (e: Exception) {
        "Unknown date"
    }
}

fun isWithinLastDays(dateString: String?, days: Long): Boolean {
    return try {
        val parsedDate = parseScanDate(dateString) ?: return false
        val now = Date().time
        val diffMillis = now - parsedDate.time
        diffMillis >= 0 && diffMillis <= days * 24 * 60 * 60 * 1000
    } catch (e: Exception) {
        false
    }
}