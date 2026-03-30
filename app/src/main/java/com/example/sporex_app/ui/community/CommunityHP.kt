package com.example.sporex_app.ui.community

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.R
import com.example.sporex_app.network.CreateReplyRequest
import com.example.sporex_app.network.PostResponse
import com.example.sporex_app.network.RetrofitClient
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode
import kotlinx.coroutines.launch
import com.example.sporex_app.useraccount.UserSession

class CommunityHP : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val darkMode = isDarkMode(this)
            SPOREX_AppTheme(darkTheme = darkMode) {

                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                var postsState by remember { mutableStateOf<List<PostResponse>>(emptyList()) }
                var loading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }

                val currentUsername = UserSession.getUsername(context)

                fun loadPosts() {
                    scope.launch {
                        loading = true
                        error = null
                        try {
                            val res = RetrofitClient.api.getPosts()
                            if (res.isSuccessful) {
                                postsState = res.body().orEmpty()
                            } else {
                                error = "Failed to load posts (${res.code()})"
                            }
                        } catch (e: Exception) {
                            error = "Network error: ${e.localizedMessage ?: "Unknown error"}"
                        } finally {
                            loading = false
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    loadPosts()
                }

                val createPostLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        loadPosts()
                    }
                }

                Scaffold(
                    topBar = { TopBar() },
                    bottomBar = { BottomNavBar(currentScreen = "community") },
                    floatingActionButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FloatingActionButton(
                                onClick = {
                                    createPostLauncher.launch(
                                        Intent(context, CreatePostActivity::class.java)
                                    )
                                },
                                containerColor = colorResource(id = R.color.sporex_black),
                                contentColor = colorResource(id = R.color.sporex_white)
                            ) {
                                Text("+", fontSize = 20.sp)
                            }

                            FloatingActionButton(
                                onClick = {
                                    context.startActivity(
                                        Intent(context, AsthmaSociety::class.java)
                                    )
                                },
                                containerColor = colorResource(id = R.color.sporex_black),
                                contentColor = colorResource(id = R.color.sporex_white)
                            ) {
                                Text("More", fontSize = 14.sp)
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { padding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        when {
                            loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(colorResource(id = R.color.sporex_green)),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            error != null -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(colorResource(id = R.color.sporex_green))
                                        .padding(16.dp),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    Text(
                                        text = error ?: "Unknown error",
                                        color = colorResource(id = R.color.sporex_white)
                                    )
                                }
                            }

                            else -> {
                                CommunityScreen(
                                    posts = postsState,
                                    currentUsername = currentUsername,
                                    onAddReply = { postId, commentText ->
                                        scope.launch {
                                            try {
                                                val res = RetrofitClient.api.addReply(
                                                    postId,
                                                    CreateReplyRequest(
                                                        user_name = currentUsername,
                                                        content = commentText
                                                    )
                                                )
                                                if (res.isSuccessful) {
                                                    loadPosts()
                                                }
                                            } catch (_: Exception) {
                                            }
                                        }
                                    },
                                    onRefresh = { loadPosts() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CommunityScreen(
    posts: List<PostResponse>,
    currentUsername: String,
    onAddReply: (String, String) -> Unit,
    onRefresh: () -> Unit
) {
    var selectedPost by remember { mutableStateOf<PostResponse?>(null) }
    var filter by remember { mutableStateOf("Popular") }
    val colors = MaterialTheme.colorScheme

    val uiPosts = posts.map { it.toCommunityPost() }

    val filteredPosts = when (filter) {
        "My Posts" -> posts.filter { it.user_name == currentUsername }
        else -> posts
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip("Popular", filter == "Popular") { filter = "Popular" }
            FilterChip("My Posts", filter == "My Posts") { filter = "My Posts" }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredPosts = when (filter) {

            "My Posts" -> postsState.value.filter {
                it.author == "You"
            }


            else -> postsState.value
        }

        LazyColumn {
            items(filteredPosts, key = { it.id }) { backendPost ->
                val post = backendPost.toCommunityPost()

                CommunityPostCard(
                    post = post,
                    showDelete = backendPost.user_name == currentUsername,
                    onLike = {
                        // local only for now
                    },
                    onDelete = {
                        // backend has no delete yet
                    },
                    onViewFull = {
                        selectedPost = backendPost
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    selectedPost?.let { post ->
        AlertDialog(
            onDismissRequest = { selectedPost = null },
            confirmButton = {},
            text = {
                FullPostView(
                    post = post,
                    currentUsername = currentUsername,
                    onAddReply = { commentText ->
                        onAddReply(post.id, commentText)
                    }
                )
            }
        )
    }
}


@Composable
fun CommunityPostCard(
    post: CommunityPost,
    showDelete: Boolean,
    onLike: () -> Unit,
    onDelete: () -> Unit,
    onViewFull: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.onSurface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(post.author, fontWeight = FontWeight.Bold, color = colors.onSurface)
                Text(post.timestamp, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }


            Spacer(modifier = Modifier.height(10.dp))

            Text(post.content, color = colors.onSurface, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(16.dp))


            // Interaction Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextButton(onClick = onLike) {
                        Text(
                            if (post.isLiked) "♥ ${post.likes}" else "♡ ${post.likes}",
                            color = colors.primary
                        )
                    }

                    TextButton(onClick = onViewFull) {
                        Text(
                            "Comments (${post.comments.size})",
                            color = colors.primary
                        )
                    }


                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
                }
            }
        }
    }
}

@Composable
fun FullPostView(
    post: PostResponse,
    currentUsername: String,
    onAddReply: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    Column {
        Text(post.user_name, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(post.content)

        Spacer(modifier = Modifier.height(12.dp))
        Text("Comments", fontWeight = FontWeight.SemiBold)

        post.replies.forEach { comment ->
            Text("${comment.user_name}: ${comment.content}")
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Add comment...") }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                if (commentText.isNotBlank()) {
                    onAddReply(commentText)
                    commentText = ""
                }
            }
        ) {
            Text("Comment")
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) colors.primary else colors.surface,
        border = BorderStroke(1.dp, colors.onSurface),
        modifier = Modifier.clickable { onClick() }
    ) {

        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) colors.onPrimary else colors.onSurface
        )
    }
}




@Preview(showBackground = true)
@Composable
fun CommunityScreenPreview() {
    CommunityScreen(
        posts = listOf(
            PostResponse(
                id = "1",
                user_name = "Preview",
                post_name = "Preview Post",
                content = "Sample post",
                created_at = "Just now",
                replies = emptyList()
            )
        ),
        currentUsername = "Preview",
        onAddReply = { _, _ -> },
        onRefresh = { }
    )
}
