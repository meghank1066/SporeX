package com.example.sporex_app.useraccount

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import androidx.lifecycle.lifecycleScope
import com.example.sporex_app.MainActivity
import com.example.sporex_app.network.LoginRequest
import com.example.sporex_app.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SPOREX_AppTheme {
            LoginScreen()
            }
        }
    }

    // Called from the composable
    fun performLogin(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body()?.success == true) {
                    val msg = response.body()?.message ?: "Login successful"
                    val prefs = getSharedPreferences("auth", MODE_PRIVATE)

                    prefs.edit()
                        .putString("user_email", email)
                        .apply()


                    onResult(true, msg)


                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    val msg = when (response.code()) {
                        403 -> "Please verify your email. Check your inbox."
                        401 -> "Invalid email or password"
                        else -> response.body()?.message ?: "Login failed (${response.code()})"
                    }

                    onResult(false, msg)
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {

    val context = LocalContext.current
    val activity = context as? LoginActivity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Login",
                        color = Color(0xFF06A546)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF06A546)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF040F0F),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true
                        activity?.performLogin(email, password) { _, message ->
                            isLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06A546),
                    contentColor = Color.White
                )
            ) {
                Text(if (isLoading) "Logging in..." else "Login", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Or continue with",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = { /* TODO: Handle Google login */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Google Login", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Don't have an account? Sign up",
                fontSize = 16.sp,
                color = Color(0xFF06A546),
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    context.startActivity(
                        Intent(context, RegisterActivity::class.java)
                    )
                }
            )
        }
    }
}