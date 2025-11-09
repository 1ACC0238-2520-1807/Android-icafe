package com.example.icafe.core.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = TokenManager.getToken() // Obtiene el token

        if (token == null) {
            // Si no hay token, procede con la solicitud original (para endpoints públicos como login/register)
            return chain.proceed(originalRequest)
        }

        // Si hay token, lo añade al encabezado Authorization
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token") // Asegura el prefijo "Bearer "
            .build()

        return chain.proceed(newRequest)
    }
}