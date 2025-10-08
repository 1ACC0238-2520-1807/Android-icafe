package com.example.icafe.core.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtiene la petición original
        val originalRequest = chain.request()

        // Obtiene el token
        val token = TokenManager.getToken()

        // Si no hay token (ej. en login/register), ejecuta la petición original
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Si hay token, crea una nueva petición con la cabecera de autorización
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Ejecuta la nueva petición
        return chain.proceed(newRequest)
    }
}