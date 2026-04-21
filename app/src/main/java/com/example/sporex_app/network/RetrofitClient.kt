package com.example.sporex_app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.sporex_app.network.RetrofitClient
import kotlinx.coroutines.launch

object RetrofitClient {


    // base URL ADDED on render

//    private const val BASE_URL = "https://sporex.onrender.com"
private const val BASE_URL = "https://sporex.onrender.com"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: SporexApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SporexApi::class.java)
    }
}
