package com.example.icafe.features.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RegisterRequest
import com.example.icafe.core.data.network.RetrofitClient
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {
    private val authApi = RetrofitClient.authApi

    // Campos del formulario
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    // Estado de la UI
    var uiState by mutableStateOf<RegisterUiState>(RegisterUiState.Idle)
        private set

    fun register() {
        if (password != confirmPassword) {
            uiState = RegisterUiState.Error("Las contraseñas no coinciden.")
            return
        }
        if (email.isBlank() || password.isBlank()) {
            uiState = RegisterUiState.Error("Email y contraseña son obligatorios.")
            return
        }

        uiState = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password
                )
                val response = authApi.register(request)

                if (response.isSuccessful) {
                    uiState = RegisterUiState.Success
                } else {
                    val errorBody = response.errorBody()?.string() ?: "El email ya está en uso o hubo un error."
                    uiState = RegisterUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                uiState = RegisterUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun clearError() {
        if(uiState is RegisterUiState.Error){
            uiState = RegisterUiState.Idle
        }
    }
}