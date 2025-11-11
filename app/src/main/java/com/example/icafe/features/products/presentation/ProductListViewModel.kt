package com.example.icafe.features.products.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.products.data.network.ProductResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(val products: List<ProductResource>) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}

class ProductListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L

    init {
        // loadProducts() // Comentado como se discutió anteriormente
    }

    fun loadProducts() {
        Log.d("ProductListViewModel", "loadProducts() llamado para branchId: $branchId")
        if (_uiState.value !is ProductListUiState.Loading) {
            _uiState.value = ProductListUiState.Loading
        }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getProductsByBranchId(branchId)
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    Log.d("ProductListViewModel", "API regresó ${products.size} productos para branchId $branchId.")
                    _uiState.value = ProductListUiState.Success(products)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar productos."
                    Log.e("ProductListViewModel", "Error al cargar productos de la API: ${response.code()} - $errorBody")
                    _uiState.value = ProductListUiState.Error(errorBody)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Error desconocido en el cuerpo de la respuesta"
                Log.e("ProductListViewModel", "HTTP Exception al cargar productos: Code=${e.code()}, Mensaje=${e.message()}, Body=${errorBody}", e)
                _uiState.value = ProductListUiState.Error("Error del servidor (${e.code()}): ${errorBody}")
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Excepción de red al cargar productos: ${e.message}", e)
                _uiState.value = ProductListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun refreshProducts() {
        Log.d("ProductListViewModel", "refreshProducts() llamado.")
        loadProducts()
    }

    // *** CAMBIO CRÍTICO: ProductListViewModelFactory ahora es un companion object ***
    companion object {
        fun ProductListViewModelFactory(portfolioId: String, selectedSedeId: String): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
                        return ProductListViewModel(
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
}