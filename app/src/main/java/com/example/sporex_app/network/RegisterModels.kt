package com.example.sporex_app.network

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)