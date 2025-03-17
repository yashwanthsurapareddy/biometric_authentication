package com.myapplication.hawcx

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthRequest(
    val email : String,
    val token : String
)
data class AuthResponse(
    val success: Boolean,
    val message: String
)
interface AuthService{
    @POST("email/validate")
    suspend fun authenticateUser(@Body request: AuthRequest): Response<AuthResponse>
}