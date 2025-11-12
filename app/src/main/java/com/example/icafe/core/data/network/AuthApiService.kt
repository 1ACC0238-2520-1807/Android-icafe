package com.example.icafe.core.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/api/v1/authentication/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/authentication/sign-up")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
}