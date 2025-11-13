package com.example.icafe.features.products.presentation

import android.util.Log // <-- Importa Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.features.products.data.network.ProductStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

// Estados de la UI para la pantalla de detalles del producto
sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: ProductResource) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
    object Deleted : ProductDetailUiState() // Para cuando un producto se elimina con éxito
    object ProductActionSuccess : ProductDetailUiState() // Para cuando una acción (archivar/activar) es exitosa
}

class ProductDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L
    private val productId: Long = savedStateHandle.get<String>("productId")?.toLongOrNull()
        ?: throw IllegalArgumentException("Product ID is required")

    init {
        loadProductDetails()
    }

    fun loadProductDetails() {
        _uiState.value = ProductDetailUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getProductById(productId)
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    if (product.branchId != branchId) {
                        _uiState.value = ProductDetailUiState.Error("El producto no pertenece a la sede seleccionada.")
                        return@launch
                    }

                    // *** AÑADIR ESTE LOG AQUÍ PARA DEPURAR LOS INGREDIENTES RECIBIDOS ***
                    product.ingredients.forEachIndexed { index, ingredient ->
                        Log.d("ViewModelIngredient", "API Response Ingredient (Index $index): supplyItemId=${ingredient.supplyItemId}, name=${ingredient.name}, unit=${ingredient.unit}, quantity=${ingredient.quantity}")
                    }

                    _uiState.value = ProductDetailUiState.Success(product)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar el producto."
                    _uiState.value = ProductDetailUiState.Error(errorBody)
                    Log.e("ProductDetailViewModel", "Error loading product details: ${response.code()} - $errorBody")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Error desconocido en el cuerpo de la respuesta"
                _uiState.value = ProductDetailUiState.Error("Error del servidor (${e.code()}): $errorBody")
                Log.e("ProductDetailViewModel", "HTTP Exception loading product details: Code=${e.code()}, Message=${e.message()}, Body=${errorBody}", e)
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Error de conexión: ${e.message}")
                Log.e("ProductDetailViewModel", "Network Exception loading product details: ${e.message}", e)
            }
        }
    }

    // Función para archivar un producto
    fun archiveProduct() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading // Mostrar estado de carga durante la acción
            try {
                val response = RetrofitClient.productApi.archiveProduct(productId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductDetailUiState.ProductActionSuccess
                    loadProductDetails() // Recargar para mostrar el estado actualizado
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al archivar producto."
                    _uiState.value = ProductDetailUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Error de conexión al archivar: ${e.message}")
            }
        }
    }

    // Función para activar un producto (desarchivar)
    fun activateProduct() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading // Mostrar estado de carga durante la acción
            try {
                val response = RetrofitClient.productApi.activateProduct(productId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProductDetailUiState.ProductActionSuccess
                    loadProductDetails() // Recargar para mostrar el estado actualizado
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al activar producto."
                    _uiState.value = ProductDetailUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Error de conexión al activar: ${e.message}")
            }
        }
    }

    // Función para eliminar un producto
    fun deleteProduct() {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading // Mostrar estado de carga durante la acción
            try {
                val response = RetrofitClient.productApi.deleteProduct(productId)
                if (response.isSuccessful) {
                    _uiState.value = ProductDetailUiState.Deleted
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al eliminar producto."
                    _uiState.value = ProductDetailUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error("Error de conexión al eliminar: ${e.message}")
            }
        }
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String, productId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
                        return ProductDetailViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("portfolioId", portfolioId)
                                set("selectedSedeId", selectedSedeId)
                                set("productId", productId.toString())
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}