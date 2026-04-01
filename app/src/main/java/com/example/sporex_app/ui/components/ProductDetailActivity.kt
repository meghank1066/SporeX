package com.example.sporex_app.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sporex_app.network.ProductDetail
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode
import kotlinx.coroutines.launch

class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val productId = intent.getStringExtra("product_id") ?: "vinegar"

        setContent {
            val darkMode = isDarkMode(this)

            SPOREX_AppTheme(darkTheme = darkMode) {
                ProductDetailScreen(productId = productId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailScreen(productId: String) {

    var detail by remember { mutableStateOf<ProductDetail?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        try {
            val res = RetrofitClient.api.getProductDetail(productId)

            if (res.isSuccessful) {
                detail = res.body()
            } else {
                error = "Failed to load (${res.code()})"
            }

        } catch (e: Exception) {
            error = "Network error: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Method & Safety") }) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F9D58))   // SporeX green background
        ) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    when {

                        loading -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                        error != null -> Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error
                        )

                        detail != null -> {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(
                                        text = detail!!.name,
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Text(
                                        text = "Best for: ${detail!!.best_for}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    Text(
                                        text = "Safety: ${detail!!.warning}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                "Steps",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(detail!!.steps) { index, step ->

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}. $step",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}