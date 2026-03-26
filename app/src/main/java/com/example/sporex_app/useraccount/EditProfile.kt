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


        val isDarkMode = com.example.sporex_app.utils.isDarkMode(this)

        setContent {
            SPOREX_AppTheme(darkTheme = isDarkMode) {
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
    ) { uri -> profileImageUri = uri }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "settings") },
        containerColor = colors.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                        tint = colors.onBackground
                    )
                }

                Text(
                    text = "Edit Profile",
                    color = colors.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            // Avatar picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(colors.surface.copy(alpha = 0.25f))
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
                        tint = colors.onSurface,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            RoundedTextField(
                value = username,
                label = "Username",
                onValueChange = { username = it },
                colors = colors
            )

            RoundedTextField(
                value = email,
                label = "Email",
                onValueChange = { email = it },
                colors = colors
            )

            Spacer(Modifier.height(16.dp))

            if (showError) {
                Text(
                    text = "Username and Email cannot be empty",
                    color = colors.error,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(32.dp))

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
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Save Changes", fontSize = 17.sp)
            }
        }
    }
}


@Composable
fun RoundedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    colors: ColorScheme
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colors.onSurface) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.onSurface.copy(alpha = 0.5f),
            cursorColor = colors.onSurface,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surface.copy(alpha = 0.25f),
            unfocusedContainerColor = colors.surface.copy(alpha = 0.18f)
        )
    )
}

@Composable
fun RoundedPasswordField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    colors: ColorScheme
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colors.onBackground) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.onBackground,
            unfocusedBorderColor = colors.onBackground.copy(alpha = 0.5f),
            cursorColor = colors.onBackground,
            focusedTextColor = colors.onBackground,
            unfocusedTextColor = colors.onBackground,
            focusedContainerColor = colors.surface.copy(alpha = 0.25f),
            unfocusedContainerColor = colors.surface.copy(alpha = 0.18f)
        )
    )
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

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "settings") },
        containerColor = colors.background
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
                        tint = colors.onBackground
                    )
                }
                Text(
                    text = "Change Password",
                    color = colors.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Text(
                text = "Keep your account secure by updating your password.",
                color = colors.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            RoundedPasswordField(
                value = currentPassword,
                label = "Current Password",
                onValueChange = { currentPassword = it },
                colors = colors
            )

            Spacer(Modifier.height(16.dp))

            RoundedPasswordField(
                value = newPassword,
                label = "New Password",
                onValueChange = {
                    newPassword = it
                    passwordStrength = calculatePasswordStrength(it)
                },
                colors = colors
            )

            Spacer(Modifier.height(12.dp))

            PasswordStrengthBar(strength = passwordStrength, colors = colors)

            Spacer(Modifier.height(16.dp))

            RoundedPasswordField(
                value = confirmPassword,
                label = "Confirm New Password",
                onValueChange = { confirmPassword = it },
                colors = colors
            )

            errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = colors.error, fontSize = 14.sp)
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
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Update Password", fontSize = 17.sp)
            }
        }
    }
}

@Composable
fun PasswordStrengthBar(strength: Float, colors: androidx.compose.material3.ColorScheme) {
    val animatedWidth by animateFloatAsState(
        targetValue = strength,
        label = "strengthAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(colors.surface.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedWidth)
                .background(
                    when {
                        strength < 0.34f -> colors.error
                        strength < 0.67f -> colors.secondary
                        else -> colors.primary
                    }
                )
                .clip(RoundedCornerShape(50.dp))
        )
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