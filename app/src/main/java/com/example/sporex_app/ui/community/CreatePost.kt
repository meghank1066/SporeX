package com.example.sporex_app.ui.community

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.R
import com.example.sporex_app.network.CreatePostRequest
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.useraccount.UserSession
import kotlinx.coroutines.launch

class CreatePostActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SPOREX_AppTheme {
                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomNavBar(currentScreen = "community") }
                ) { padding ->
                    CreatePostScreen(
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier
) {
    var postContent by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val currentUsername = UserSession.getUsername(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.sporex_green))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create Post",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.sporex_black)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.sporex_white)
            ),
            border = BorderStroke(
                2.dp,
                colorResource(id = R.color.sporex_black)
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "What's on your mind?",
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.sporex_black)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = postContent,
                    onValueChange = {
                        postContent = it
                        if (error != null) error = null
                    },
                    placeholder = {
                        Text(
                            "Share your mold experience or ask for advice...",
                            color = colorResource(id = R.color.sporex_text_muted)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorResource(id = R.color.sporex_black),
                        unfocusedBorderColor = colorResource(id = R.color.sporex_black),
                        cursorColor = colorResource(id = R.color.sporex_black)
                    )
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (activity == null) return@Button

                        scope.launch {
                            loading = true
                            error = null

                            try {
                                val res = RetrofitClient.api.createPost(
                                    CreatePostRequest(
                                        user_name = currentUsername,
                                        post_name = "Community Post",
                                        content = postContent.trim()
                                    )
                                )

                                if (res.isSuccessful) {
                                    activity.setResult(Activity.RESULT_OK)
                                    activity.finish()
                                } else {
                                    error = "Failed to create post (${res.code()})"
                                }
                            } catch (e: Exception) {
                                error = "Network error: ${e.localizedMessage ?: "Unknown error"}"
                            } finally {
                                loading = false
                            }
                        }
                    },
                    enabled = postContent.isNotBlank() && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.sporex_black),
                        contentColor = colorResource(id = R.color.sporex_white)
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = colorResource(id = R.color.sporex_white)
                        )
                    } else {
                        Text("Post")
                    }
                }
            }
        }
    }
}