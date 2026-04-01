package com.example.sporex_app.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {
                HistoryScreen(
                    cases = listOf(
                        Case("11/02/2025 17:32", "OPEN CASE", "49% Chance of Aspergillus"),
                        Case("10/02/25 13:23", "OPEN CASE", "49% Chance of Aspergillus + 10% chance of Penicillium"),
                        Case("05/01/2025 12:42", "SOLVED", "10% Chance of Cladosporium, 30% Chance of Penicillium")
                    ),
                    onCaseClick = { /* Handle case click */ }
                )
            }
        }
    }

    @Composable
    fun HistoryScreen(
        cases: List<Case>,
        onCaseClick: (Case) -> Unit
    ) {
        val colors = MaterialTheme.colorScheme

        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomNavBar(currentScreen = "history") },
            containerColor = colors.primary
        ) { padding ->
            // REMOVED the extra Surface with MaterialTheme.shapes.large
            // That was what was causing the white corners/edges.

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Last 7 Days",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cases.filter { it.result.startsWith("OPEN") }) { case ->
                            CaseCard(case, onCaseClick)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Last Month",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cases.filter { it.result.startsWith("SOLVED") }) { case ->
                            CaseCard(case, onCaseClick)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CaseCard(
        case: Case,
        onCaseClick: (Case) -> Unit
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCaseClick(case) },
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(case.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        case.result,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        case.chance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "View Case",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

