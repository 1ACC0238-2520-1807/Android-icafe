package com.example.icafe.features.finances.presentation.sales

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.finances.data.network.CreateSaleRequest
import com.example.icafe.features.finances.data.network.SaleItemRequest
import com.example.icafe.features.inventory.data.network.CreateInventoryTransactionResource
import com.example.icafe.features.inventory.data.network.TransactionType
import com.example.icafe.features.products.data.network.ProductResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import android.util.Log // Import for logging

// --- ViewModels y Estados de UI ---
sealed class AddSaleUiState {
    object Loading : AddSaleUiState()
    object ReadyForInput : AddSaleUiState()
    data class Success(val message: String) : AddSaleUiState()
    data class Error(val message: String) : AddSaleUiState()
    object Processing : AddSaleUiState() // Nuevo estado para indicar que una operación está en curso
}

// Clase de datos para representar un ítem de venta en el formulario
data class SaleItemForm(
    val product: ProductResource, // El producto completo
    var quantity: String, // Cantidad en String para TextField
    var unitPrice: String // Precio unitario en String para TextField
) {
    val subtotal: Double
        get() = (quantity.toIntOrNull() ?: 0) * (unitPrice.toDoubleOrNull() ?: 0.0)
}

class AddSaleViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<AddSaleUiState>(AddSaleUiState.Loading)
    val uiState: StateFlow<AddSaleUiState> = _uiState.asStateFlow()

    // Para eventos no consumibles como SnackBar messages
    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    val branchId: Long = savedStateHandle.get<String>("selectedSedeId")?.toLongOrNull() ?: 1L
    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!

    var customerId by mutableStateOf("")
    var notes by mutableStateOf("")

    private val _availableProducts = MutableStateFlow<List<ProductResource>>(emptyList())
    val availableProducts: StateFlow<List<ProductResource>> = _availableProducts.asStateFlow()

    // Lista mutable de ítems de venta seleccionados en el formulario
    private val _selectedSaleItems = mutableStateListOf<SaleItemForm>() // Usar mutableStateListOf para una lista observable
    val selectedSaleItems: List<SaleItemForm> get() = _selectedSaleItems // Exponer como List inmutable

    val totalAmount: Double
        get() = _selectedSaleItems.sumOf { it.subtotal }

    // Variable para prevenir envíos múltiples, observada por la UI
    var isSubmitting by mutableStateOf(false)
        private set

    init {
        loadAvailableProducts()
    }

    private fun loadAvailableProducts() {
        viewModelScope.launch {
            _uiState.value = AddSaleUiState.Loading
            try {
                // Obtener productos de la API para la sede actual
                val response = RetrofitClient.productApi.getProductsByBranchId(branchId)
                if (response.isSuccessful && response.body() != null) {
                    _availableProducts.value = response.body()!!
                    _uiState.value = AddSaleUiState.ReadyForInput
                } else {
                    _uiState.value = AddSaleUiState.Error("Error al cargar productos: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _uiState.value = AddSaleUiState.Error("Error de conexión al cargar productos: ${e.message}")
            }
        }
    }

    // Añade un producto a la lista de ítems de venta
    fun addProductToSale(product: ProductResource) {
        if (_selectedSaleItems.none { it.product.id == product.id }) {
            _selectedSaleItems.add(SaleItemForm(product, "1", product.salePrice.toString()))
        } else {
            viewModelScope.launch { _events.emit("El producto ya fue agregado.") }
        }
    }

    // Actualiza la cantidad de un ítem de venta en la lista
    fun updateSaleItemQuantity(index: Int, quantity: String) {
        if (index in _selectedSaleItems.indices) {
            _selectedSaleItems[index] = _selectedSaleItems[index].copy(quantity = quantity)
        }
    }

    // Elimina un ítem de venta de la lista
    fun removeSaleItem(index: Int) {
        _selectedSaleItems.removeAt(index)
    }

    // Registra la venta y los movimientos de inventario asociados
    fun registerSale() {
        // Bloquear si ya se está procesando
        if (isSubmitting) {
            Log.d("AddSaleVM", "Submission already in progress, ignoring duplicate call.")
            return
        }

        if (customerId.isBlank() || _selectedSaleItems.isEmpty()) {
            _uiState.value = AddSaleUiState.Error("El ID del cliente y al menos un producto son obligatorios.")
            return
        }

        val saleItemsRequest = _selectedSaleItems.map { item ->
            SaleItemRequest(
                productId = item.product.id,
                quantity = item.quantity.toIntOrNull() ?: 0,
                unitPrice = item.unitPrice.toDoubleOrNull() ?: 0.0
            )
        }

        _uiState.value = AddSaleUiState.Processing // Establecer estado de procesamiento
        isSubmitting = true // Activar bandera de envío

        viewModelScope.launch {
            try {
                Log.d("AddSaleVM", "Attempting to create sale...")
                val request = CreateSaleRequest(
                    customerId = customerId.toLongOrNull() ?: 0L,
                    branchId = branchId,
                    items = saleItemsRequest,
                    notes = notes.ifBlank { null }
                )
                val response = RetrofitClient.salesApi.createSale(request)

                if (response.isSuccessful && response.body() != null) {
                    val saleResource = response.body()!!
                    Log.d("AddSaleVM", "Sale created (ID: ${saleResource.id}). Now registering inventory movements for each product sold.")

                    for (saleItem in saleResource.items) {
                        val productResponse = RetrofitClient.productApi.getProductById(saleItem.productId)
                        if (productResponse.isSuccessful && productResponse.body() != null) {
                            val product = productResponse.body()!!
                            Log.d("AddSaleVM", "  Processing product '${product.name}' (ID: ${product.id}) for sale item (Qty: ${saleItem.quantity})...")
                            for (ingredient in product.ingredients) {
                                Log.d("AddSaleVM", "    Registering SALIDA movement for ingredient '${ingredient.name}' (SupplyItemID: ${ingredient.supplyItemId}) - Quantity: ${ingredient.quantity * saleItem.quantity}")
                                val transaction = CreateInventoryTransactionResource(
                                    supplyItemId = ingredient.supplyItemId,
                                    branchId = branchId,
                                    type = TransactionType.SALIDA,
                                    quantity = ingredient.quantity * saleItem.quantity,
                                    origin = "Venta de Producto '${product.name}' (ID: ${product.id})"
                                )
                                // Es buena práctica verificar la respuesta de esta llamada a la API también.
                                val movementResponse = RetrofitClient.inventoryApi.registerMovement(transaction)
                                if (!movementResponse.isSuccessful) {
                                    Log.e("AddSaleVM", "Failed to register inventory movement for ingredient ${ingredient.name}: ${movementResponse.code()} - ${movementResponse.errorBody()?.string()}")
                                    // Opcionalmente, puedes establecer _uiState a error aquí y detener el procesamiento.
                                    // Por ahora, solo se registra el error.
                                }
                            }
                        } else {
                            Log.e("AddSaleVM", "Failed to fetch product details for product ID: ${saleItem.productId}. Sale item quantity: ${saleItem.quantity}")
                        }
                    }
                    _uiState.value = AddSaleUiState.Success("Venta registrada exitosamente. ID: ${saleResource.id}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al registrar venta."
                    Log.e("AddSaleVM", "Error registering sale: ${response.code()} - $errorBody")
                    _uiState.value = AddSaleUiState.Error("Error al registrar venta: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("AddSaleVM", "Connection error during sale registration: ${e.message}", e)
                _uiState.value = AddSaleUiState.Error("Error de conexión: ${e.message}")
            } finally {
                isSubmitting = false // Asegurarse de que la bandera se restablezca siempre
            }
        }
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AddSaleViewModel::class.java)) {
                        return AddSaleViewModel(
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