package com.example.icafe.features.inventory.presentation.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.ItemResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ItemListUiState {
    object Loading : ItemListUiState()
    data class Success(val items: List<ItemResource>) : ItemListUiState()
    data class Error(val message: String) : ItemListUiState()
}

class ItemListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ItemListUiState>(ItemListUiState.Loading)
    val uiState: StateFlow<ItemListUiState> = _uiState

    init {
        loadItems()
    }

    fun loadItems() {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.inventoryApi.getItems()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ItemListUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ItemListUiState.Error("Error al cargar insumos.")
                }
            } catch (e: Exception) {
                _uiState.value = ItemListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}