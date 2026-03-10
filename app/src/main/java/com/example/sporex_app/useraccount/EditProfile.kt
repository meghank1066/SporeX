package com.example.sporex_app.useraccount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.R
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar

class EditProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUsername = intent.getStringExtra("username") ?: ""
        val currentEmail = intent.getStringExtra("email") ?: ""


        setContent {
            SPOREX_AppTheme {
                EditProfileScreen(
                    currentUsername = currentUsername,
                    currentEmail = currentEmail,
                    onBack = { finish() },
                    onSave = { updatedUsername, updatedEmail, updatedPassword ->

                        val resultIntent = Intent().apply {
                            putExtra("username", updatedUsername)
                            putExtra("email", updatedEmail)
                            putExtra("password", updatedPassword)
                        }

                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    currentUsername: String,
    currentEmail: String,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var username by remember { mutableStateOf(currentUsername) }
    var email by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        profileImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        color = colorResource(id = R.color.sporex_green)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(id = R.color.sporex_green)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Avatar picker (single component)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(colorResource(id = R.color.sporex_grey))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {

                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Icon",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showError) {
                Text(
                    text = "Username and Email cannot be empty",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        onSave(username, email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sporex_green),
                    contentColor = Color.White
                )
            ) {
                Text("Save Changes", fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordStrength by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "settings") },
        containerColor = colorResource(id = R.color.sporex_black)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // --- Back button row ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colorResource(id = R.color.sporex_white)
                    )
                }
                Text(
                    text = "Change Password",
                    color = colorResource(id = R.color.sporex_white),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Text(
                text = "Keep your account secure by updating your password.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            RoundedPasswordField(
                value = currentPassword,
                label = "Current Password",
                onValueChange = { currentPassword = it }
            )

            Spacer(Modifier.height(16.dp))

            RoundedPasswordField(
                value = newPassword,
                label = "New Password",
                onValueChange = {
                    newPassword = it
                    passwordStrength = calculatePasswordStrength(it)
                }
            )

            Spacer(Modifier.height(12.dp))

            PasswordStrengthBar(passwordStrength)

            Spacer(Modifier.height(16.dp))

            RoundedPasswordField(
                value = confirmPassword,
                label = "Confirm New Password",
                onValueChange = { confirmPassword = it }
            )

            errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Color(0xFFFFB3B3), fontSize = 14.sp)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() ||
                                newPassword.isBlank() ||
                                confirmPassword.isBlank() ->
                            errorMessage = "All fields are required"

                        newPassword != confirmPassword ->
                            errorMessage = "New passwords do not match"

                        newPassword.length < 6 ->
                            errorMessage = "Password must be at least 6 characters"

                        else -> {
                            errorMessage = null
                            onSave(newPassword)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sporex_green),
                    contentColor = Color.Black
                )
            ) {
                Text("Update Password", fontSize = 17.sp)
            }
        }
    }
}



    @Composable
fun RoundedPasswordField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.25f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.18f)

        )
    )
}

@Composable
fun PasswordStrengthBar(strength: Float) {

    val animatedWidth by animateFloatAsState(
        targetValue = strength,
        label = "strengthAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.Black.copy(alpha = 0.25f))
    ) {

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedWidth)
                .background(
                    when {
                        strength < 0.34f -> Color.Red
                        strength < 0.67f -> Color.Yellow
                        else -> Color(0xFF00E676)
                    }
                )
                .clip(RoundedCornerShape(50.dp))
        )
    }
}

fun calculatePasswordStrength(password: String): Float {
    var score = 0

    if (password.length >= 6) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++

    return score / 3f
}

class EditPasswordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SPOREX_AppTheme {
                EditPasswordScreen(
                    onBack = { finish() },
                    onSave = { newPassword ->
                        val resultIntent = Intent().apply {
                            putExtra("new_password", newPassword)
                        }

                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                )
            }
        }
    }
}