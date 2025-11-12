package com.example.icafe.core.data.network

import okhttp3.Interceptor
import okhttp3.Response
// import android.util.Log // Se puede eliminar si no se usa en ninguna otra parte del archivo

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = TokenManager.getToken() // Obtiene el token

        if (token == null) {
            // Log.w("AuthInterceptor", "No hay token disponible. La solicitud continuará sin autenticación para URL: ${originalRequest.url.encodedPath}") // Comentado
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Log.d("AuthInterceptor", "Token Bearer añadido al encabezado para URL: ${originalRequest.url.encodedPath}") // Comentado
        return chain.proceed(newRequest)
    }
}