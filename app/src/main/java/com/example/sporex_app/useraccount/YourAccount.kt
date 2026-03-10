package com.example.sporex_app.useraccount

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import androidx.compose.runtime.setValue
// 🎨 Sporex Colors
private val SporexGreen = Color(0xFF00C853)
private val SporexCard = Color(0xFF00241C)

@Composable
fun YourAccountScreen() {

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "settings") },
        containerColor = SporexGreen
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SporexCard
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {

                    AccountOptionItem(
                        title = "Edit Profile",
                        onClick = {
                            val intent = Intent(context, EditProfileActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    AccountOptionItem(
                        title = "Change Password",
                        onClick = {
                            val intent = Intent(context, EditPasswordActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    AccountOptionItem(
                        title = "Delete Account",
                        onClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text("This action is permanent. Type DELETE to confirm.")

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmText,
                        onValueChange = { confirmText = it },
                        label = { Text("Type DELETE") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (confirmText == "DELETE") {

                            context.getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()

                            val intent = Intent(context, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }

                            context.startActivity(intent)
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
private fun AccountOptionItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White
        )
    }
}