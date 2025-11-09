package com.example.icafe.features.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.LoginRequest
import com.example.icafe.core.data.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.icafe.core.data.network.TokenManager

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userId: Long) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val authApi = RetrofitClient.authApi

    // Campos del formulario
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // Estado de la UI
    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            uiState = LoginUiState.Error("Email y contraseña no pueden estar vacíos.")
            return
        }

        uiState = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = authApi.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    TokenManager.saveToken(loginResponse.token)
                    println("Login exitoso! Token: ${loginResponse.token}")
                    uiState = LoginUiState.Success(loginResponse.userId)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Credenciales incorrectas o usuario no encontrado."
                    println("Error en login: ${response.code()} - $errorBody")
                    uiState = LoginUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = LoginUiState.Error("No se pudo conectar al servidor. Revisa tu conexión.")
            }
        }
    }

    fun clearError() {
        if (uiState is LoginUiState.Error) {
            uiState = LoginUiState.Idle
        }
    }
}