package com.example.sporex_app.network

import com.example.sporex_app.settings.BasicResponse
import com.example.sporex_app.settings.SettingsResponse
import com.example.sporex_app.settings.UpdateSettingsRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface SporexApi {

    @POST("api/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("api/products")
    suspend fun getProducts(): Response<List<ProductSummary>>

    @GET("api/products/{id}")
    suspend fun getProductDetail(@Path("id") id: String): Response<ProductDetail>

    @GET("api/settings/{email}")
    suspend fun getSettings(
        @Path("email") email: String
    ): Response<SettingsResponse>

    @POST("api/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<Map<String, Any>>

    @PUT("api/settings")
    suspend fun updateSettings(
        @Body request: UpdateSettingsRequest
    ): BasicResponse

    @Multipart
    @POST("api/predict")
    suspend fun predictImage(
        @Part file: MultipartBody.Part,
        @Part("email") email: RequestBody? = null
    ): Response<PredictResponseDto>
}