package com.example.sporex_app.useraccount

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.R
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme

class UserSettings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SPOREX_AppTheme {
                UserSettingsScreen()
            }
        }
    }
}

@Composable
fun UserSettingsScreen() {

    val context = LocalContext.current

    var isDarkMode by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(false) }
    var isDataPersonalisationEnabled by remember { mutableStateOf(false) }

    // Modal states
    var showAppCustomisationDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showDataDialog by remember { mutableStateOf(false) }

    val userEmail = remember {
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isEmpty()) return@LaunchedEffect

        try {
            val response = RetrofitClient.api.getSettings(userEmail)
            if (response.isSuccessful) {
                val settings = response.body()?.settings
                settings?.let {
                    isDarkMode = it.dark_mode
                    isNotificationsEnabled = it.notifications_enabled
                    isDataPersonalisationEnabled = it.data_personalisation
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.sporex_green))
    ) {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomNavBar(currentScreen = "settings") },
            containerColor = Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxSize()
                    .background(
                        color = colorResource(id = R.color.sporex_black),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {


                Text(
                    text = "Appearence",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsOption("Your Account") {
                    context.startActivity(
                        android.content.Intent(
                            context,
                            YourAccountActivity::class.java
                        )
                    )
                }

                SettingsOption("App Customisation") {
                    showAppCustomisationDialog = true
                }

                SettingsOption("Data Personalisation") {
                    showDataDialog = true
                }

                SettingsOption("Notifications") {
                    showNotificationsDialog = true
                }

                SettingsOption("Log out") { }
            }
        }
    }

    // ---------- APP CUSTOMISATION MODAL ----------
    if (showAppCustomisationDialog) {
        AlertDialog(
            onDismissRequest = { showAppCustomisationDialog = false },
            confirmButton = {},
            title = { Text("App Customisation") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Dark Mode",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it }
                    )
                }
            }
        )
    }

    // ---------- DATA PERSONALISATION MODAL ----------
    if (showDataDialog) {
        AlertDialog(
            onDismissRequest = { showDataDialog = false },
            confirmButton = {},
            title = { Text("Data Personalisation") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enable Data Personalisation",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isDataPersonalisationEnabled,
                        onCheckedChange = { isDataPersonalisationEnabled = it }
                    )
                }
            }
        )
    }

    // ---------- NOTIFICATIONS MODAL ----------
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            confirmButton = {},
            title = { Text("Notifications") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enable Notifications",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isNotificationsEnabled,
                        onCheckedChange = { isNotificationsEnabled = it }
                    )
                }
            }
        )
    }
}

@Composable
fun SettingsOption(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(id = R.color.sporex_white),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to $label",
            tint = colorResource(id = R.color.sporex_white)
        )
    }
}