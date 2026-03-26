package com.example.sporex_app.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.sporex_app.R
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar

@Composable
fun EditDeviceScreen(
    deviceName: String,
    onRename: (String) -> Unit,
    onBackClick: () -> Unit,

    onTestConnectionClick: () -> Unit
) {
    var showRenameDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "device") },
        containerColor = colorResource(id = R.color.sporex_green)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = deviceName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface // use theme color instead of colorResource
                    )
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Settings
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingItem("Edit Device Name") { showRenameDialog = true }
                    SettingItem("Device Details") { /* TODO */ }
                    SettingItem("Test Connection") {  onTestConnectionClick() }
                }

                Spacer(Modifier.weight(1f))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Reset */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.sporex_blue),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Reset Device")
                    }

                    Button(
                        onClick = { /* TODO: Remove */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.sporex_red),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Remove Device")
                    }
                }
            }


            if (showRenameDialog) {
                RenameDeviceDialog(
                    currentName = deviceName,
                    onConfirm = { newName ->
                        onRename(newName)
                        showRenameDialog = false
                        onBackClick()
                    },
                    onCancel = { showRenameDialog = false }
                )
            }
        }
    }
}

@Composable
fun SettingItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = ">",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun RenameDeviceDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Edit Device Name",
                color = colorResource(id = R.color.sporex_white)
            )
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = colorResource(id = R.color.sporex_green_soft),
                    unfocusedLabelColor = colorResource(id = R.color.sporex_text_muted),
                    cursorColor = colorResource(id = R.color.sporex_white)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newName) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sporex_green)
                )
            ) {
                Text("Save", color = colorResource(id = R.color.sporex_white))
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sporex_black)
                )
            ) {
                Text("Cancel", color = colorResource(id = R.color.sporex_white))
            }
        },
        containerColor = colorResource(id = R.color.sporex_black),
        shape = RoundedCornerShape(12.dp)
    )
}
