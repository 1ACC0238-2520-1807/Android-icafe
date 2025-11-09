package com.example.icafe.features.products.presentation

import androidx.lifecycle.SavedStateHandle
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

class ProductListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!! // MODIFIED: Extract portfolioId
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L // Convert to Long

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductListUiState.Loading
        viewModelScope.launch {
            try {
                // Use existing API endpoint to filter by branchId
                val response = RetrofitClient.productApi.getProductsByBranchId(branchId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductListUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al cargar productos."
                    _uiState.value = ProductListUiState.Error(errorBody)
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

// DEFINITION OF THE FACTORY - AT THE END OF THE SAME VIEWMODEL FILE
class ProductListViewModelFactory(
    private val portfolioId: String, // MODIFIED: Added portfolioId
    private val selectedSedeId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId) // MODIFIED: Set portfolioId
                    set("selectedSedeId", selectedSedeId)
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}