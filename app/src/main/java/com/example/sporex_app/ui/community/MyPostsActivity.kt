package com.example.sporex_app.ui.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.network.PostResponse
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.utils.isDarkMode

@OptIn(ExperimentalMaterial3Api::class)
class MyPostsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {
                val colors = MaterialTheme.colorScheme
                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomNavBar(currentScreen = "community") },
                    containerColor = colors.primary
                ) { padding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        color = colors.primary,
                        shape = MaterialTheme.shapes.large
                    ) {
                        MyPostsScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun MyPostsScreen() {
    val colors = MaterialTheme.colorScheme
    val posts = remember {
        mutableStateListOf(
            Post(1, "You", "Just found some mold behind the couch 😱", "2h ago")
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colors.background)
    ) {
        items(posts) { post ->
            PostCardModern(post)
        }
    }
}

@Composable
fun PostCardModern(post: Post) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(colors.surface, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(post.author, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                Text(post.timestamp, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = post.content,
            fontSize = 14.sp,
            color = colors.onSurface
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text("Comment", color = colors.onSurfaceVariant, fontSize = 13.sp)
            Text("Share", color = colors.onSurfaceVariant, fontSize = 13.sp)
            Text("Like", color = colors.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}