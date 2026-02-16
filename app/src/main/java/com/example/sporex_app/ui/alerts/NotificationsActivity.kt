package com.example.sporex_app.ui.alerts


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar

class NotificationsActivity : ComponentActivity() {

    private val notificationsList = listOf(
        NotificationItem(
            title = "Air Quality Warning",
            message = "CO₂ levels are higher than recommended. Consider ventilating the room.",
            time = "5 mins ago"
        ),
        NotificationItem(
            title = "Mold Detected",
            message = "Potential mold detected on the living room wall. Check immediately.",
            time = "12 mins ago"
        ),
        NotificationItem(
            title = "CO₂ Normalized",
            message = "CO₂ levels have returned to a safe range. Good job ventilating!",
            time = "1 hour ago"
        ),

        NotificationItem(
            title = "Reminder: Air Check",
            message = "Don't forget to check the air quality in your bedroom today.",
            time = "Yesterday"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NotificationsScreen(notificationsList)
            }
        }
    }
}

@Composable
fun NotificationsScreen(notifications: List<NotificationItem>) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "home") },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(notification)
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = notification.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = notification.message,
            fontSize = 14.sp,
            color = Color(0xFF555555)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = notification.time,
            fontSize = 12.sp,
            color = Color(0xFF999999),
            modifier = Modifier.align(Alignment.End)
        )
    }
}
