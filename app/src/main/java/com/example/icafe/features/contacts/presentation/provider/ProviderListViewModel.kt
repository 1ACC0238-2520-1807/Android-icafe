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

    init {
        loadProviders()
    }

    private fun loadProviders() {
        _uiState.value = ProviderListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.contactsApi.getProviders(portfolioId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProviderListUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProviderListUiState.Error("Error al cargar proveedores.")
                }
            } catch (e: Exception) {
                _uiState.value = ProviderListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}