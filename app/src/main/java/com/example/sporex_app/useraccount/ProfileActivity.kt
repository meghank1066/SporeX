
package com.example.sporex_app.useraccount

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.sporex_app.R
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.ui.community.CommunityHP
import com.example.sporex_app.ui.components.HistoryActivity
import com.example.sporex_app.ui.device.DeviceActivity
import com.example.sporex_app.ui.navigation.TopBar


class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val username = intent.getStringExtra("username") ?: "User"

        setContent {
            SPOREX_AppTheme {
//                ProfileScreen(
//                    username = username,
//                    onHistoryClick = { startActivity(Intent(this, HistoryActivity::class.java)) },
//                    onPostsClick = { startActivity(Intent(this, CommunityHP::class.java)) },
//                    onDeviceClick = { startActivity(Intent(this, DeviceActivity::class.java)) },
//                    onSettingsClick = { startActivity(Intent(this, UserSettings::class.java)) }
//                )
                ProfileScreen(
                    username = username,
                    onHistoryClick = { startActivity(Intent(this, HistoryActivity::class.java)) },
                    onPostsClick = { startActivity(Intent(this, CommunityHP::class.java)) },
                    onDeviceClick = { startActivity(Intent(this, DeviceActivity::class.java)) },
                    onSettingsClick = { startActivity(Intent(this, UserSettings::class.java)) },
                    onEditProfileClick = {
                        val intent = Intent(this, EditProfileActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ProfileScreen(
    username: String,
    onHistoryClick: () -> Unit,
    onPostsClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "profile") },
        containerColor = Color.Transparent
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.sporex_green))
                .padding(
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LayoutDirection.Ltr),
                    end = padding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                TopBar()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    color = Color.Transparent
                ) {
                    ProfileContent(
                        username = username,
                        onHistoryClick = onHistoryClick,
                        onPostsClick = onPostsClick,
                        onDeviceClick = onDeviceClick,
                        onSettingsClick = onSettingsClick,
                        onEditProfileClick = onEditProfileClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    username: String,
    onHistoryClick: () -> Unit,
    onPostsClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.BottomEnd
        ) {

            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.chloekim),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )

            // Edit Button Overlay
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.sporex_black))
                    .clickable { onEditProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileActionCard("Community", Icons.Default.Article, onPostsClick)
                ProfileActionCard("History", Icons.Default.History, onHistoryClick)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileActionCard("My Device", Icons.Default.Devices, onDeviceClick)
                ProfileActionCard("Settings", Icons.Default.Settings, onSettingsClick)
            }
        }

    }
}



@Composable
private fun ProfileActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(160.dp)
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF2F2F2),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(56.dp),
                tint = Color(0xFF08A045)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )}
    }
}