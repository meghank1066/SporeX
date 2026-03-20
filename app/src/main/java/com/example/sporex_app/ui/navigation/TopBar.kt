package com.example.sporex_app.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sporex_app.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.example.sporex_app.ui.theme.TopBarFont
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.sporex_app.ui.alerts.NotificationsActivity

@Composable
fun TopBar() {

    val context = LocalContext.current

    Column {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
//                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
//                .background(colorResource(id = R.color.sporex_grey))
                .height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .background(MaterialTheme.colorScheme.primary)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(colorResource(id = R.color.sporex_grey))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(28.dp))

                Text(
                    text = "SPOREX",
                    fontFamily = TopBarFont,
                    fontSize = 32.sp,
                    color = colorResource(id = R.color.sporex_green)
                )

                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            // Navigate to NotificationsActivity
                            val intent = Intent(context, NotificationsActivity::class.java)
                            context.startActivity(intent)
                        },
                    tint = colorResource(id = R.color.sporex_green)
                )
            }
        }
    }
}
