package com.example.sporex_app.ui.navigation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sporex_app.MainActivity
import com.example.sporex_app.ui.components.UploadActivity
import com.example.sporex_app.ui.device.DeviceActivity
import com.example.sporex_app.ui.community.CommunityHP
import com.example.sporex_app.useraccount.ProfileActivity

@Composable
fun BottomNavBar(currentScreen: String) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp)
                .offset(y = (-10).dp), // moved up more
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // HOME
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)   // bigger
                    .clickable { context.startActivity(Intent(context, MainActivity::class.java)) }
            )

            // DEVICE
            Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = "Devices",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { context.startActivity(Intent(context, DeviceActivity::class.java)) }
            )

            // CAMERA (same size, but styled)
            Box(
                modifier = Modifier
                    .size(55.dp) // slightly bigger but aligned
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(3.dp, Color.White, CircleShape)
                    .clickable {
                        context.startActivity(Intent(context, UploadActivity::class.java))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp) // same icon size as others
                )
            }

            // COMMUNITY
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = "Community",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { context.startActivity(Intent(context, CommunityHP::class.java)) }
            )

            // PROFILE
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { context.startActivity(Intent(context, ProfileActivity::class.java)) }
            )
        }
    }
}
