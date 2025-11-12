package com.example.icafe.features.contacts.presentation.provider

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.ProviderResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProviderListUiState {
    object Loading : ProviderListUiState()
    data class Success(val providers: List<ProviderResource>) : ProviderListUiState()
    data class Error(val message: String) : ProviderListUiState()
}

class ProviderListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<ProviderListUiState>(ProviderListUiState.Loading)
    val uiState: StateFlow<ProviderListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!! // Mantenido por consistencia, no utilizado directamente por Proveedores

    init {
        loadProviders()
    }

    private fun loadProviders() {
        _uiState.value = ProviderListUiState.Loading
        viewModelScope.launch {
            try {
                // Obtener TODOS los proveedores para el portfolioId
                val response = RetrofitClient.contactsApi.getProviders(portfolioId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProviderListUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al cargar proveedores."
                    _uiState.value = ProviderListUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = ProviderListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    class ProviderListViewModelFactory(
        private val portfolioId: String,
        private val selectedSedeId: String // No se usa directamente en este ViewModel, pero es necesario para la consistencia
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProviderListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProviderListViewModel(
                    savedStateHandle = SavedStateHandle().apply {
                        set("portfolioId", portfolioId)
                        set("selectedSedeId", selectedSedeId)
                    }
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}