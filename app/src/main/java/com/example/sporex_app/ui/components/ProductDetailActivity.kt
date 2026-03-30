package com.example.sporex_app.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val scope = rememberCoroutineScope()

    var detail by remember { mutableStateOf<ProductDetail?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        scope.launch {
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
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Method & Safety") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                loading -> CircularProgressIndicator()
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                detail != null -> {
                    Text(detail!!.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(10.dp))

                    Text("Best for: ${detail!!.best_for}")
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Safety: ${detail!!.warning}",
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(Modifier.height(16.dp))
                    Text("Steps", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(detail!!.steps) { index, step ->
                            Card(Modifier.fillMaxWidth()) {
                                Text(
                                    text = "${index + 1}. $step",
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
