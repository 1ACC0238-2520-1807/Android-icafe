package com.example.icafe.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.products.data.network.ProductResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(val products: List<ProductResource>) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}

class ProductListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductListUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProductListUiState.Error("Error al cargar productos.")
                }
            } catch (e: Exception) {
                _uiState.value = ProductListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun refreshProducts() {
        loadProducts()
    }
}
