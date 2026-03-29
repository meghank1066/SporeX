package com.example.sporex_app.network

data class ReplyResponse(
    val user_name: String,
    val content: String,
    val created_at: String?
)

data class PostResponse(
    val id: String,
    val user_name: String,
    val post_name: String,
    val content: String,
    val created_at: String?,
    val replies: List<ReplyResponse> = emptyList()
)

data class CreatePostRequest(
    val user_name: String,
    val post_name: String,
    val content: String
)

data class CreateReplyRequest(
    val user_name: String,
    val content: String
)

data class BasicResponse(
    val success: Boolean,
    val message: String
)