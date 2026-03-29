package com.example.sporex_app.ui.components

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sporex_app.network.ProductSummary
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import kotlinx.coroutines.launch



class ProductsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SPOREX_AppTheme {
                ProductsScreen(
                    onSelect = { productId ->
                        startActivity(
                            Intent(this, ProductDetailActivity::class.java).apply {
                                putExtra("product_id", productId)
                            }
                        )
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(onSelect: (String) -> Unit) {
    val scope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<ProductSummary>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = RetrofitClient.api.getProducts()
                if (res.isSuccessful) {
                    products = res.body().orEmpty()
                } else {
                    error = "Failed to load products (${res.code()})"
                }
            } catch (e: Exception) {
                error = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recommended Products",
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F9D58))   // SporeX green
        ) {

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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

                        else -> LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(products) { p ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelect(p.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )

                                ) {

                                    Column(Modifier.padding(16.dp)) {

                                        Text(
                                            p.name,
                                            style = MaterialTheme.typography.titleMedium,
                                                    color = Color.Black
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            text = "Best for: ${p.best_for}",
                                            color = Color.Black
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            if (p.sustainable)
                                                "Sustainable option ✅"
                                            else
                                                "Standard option",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF0F9D58)
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


