package com.example.icafe.core.data.network

import com.google.gson.annotations.SerializedName

// Modelo para enviar en la petición de Login (el username es el email)
data class LoginRequest(
    @SerializedName("email") // Corregido: El backend espera "email", no "username"
    val email: String,
    val password: String
)

// Modelo que esperamos recibir como respuesta del Login
data class LoginResponse(
    @SerializedName("id")
    val userId: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("token")
    val token: String
    // El backend no envía los roles en la respuesta del login
)

// Modelo para la petición de Registro (SOLO email y password)
data class RegisterRequest(
    val email: String,
    val password: String
    // No se envían más campos. El backend asigna el rol y crea el portfolio.
)