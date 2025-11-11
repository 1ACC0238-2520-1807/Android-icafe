package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.features.finances.data.network.CreatePurchaseOrderRequest
import com.example.icafe.features.inventory.data.network.CreateInventoryTransactionResource
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.inventory.data.network.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.lang.Exception // Asegúrate de importar esto

// --- ViewModels y Estados de UI ---
sealed class AddPurchaseOrderUiState {
    object Loading : AddPurchaseOrderUiState()
    object ReadyForInput : AddPurchaseOrderUiState()
    data class Success(val message: String) : AddPurchaseOrderUiState()
    data class Error(val message: String) : AddPurchaseOrderUiState()
}

class AddPurchaseOrderViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPurchaseOrderUiState>(AddPurchaseOrderUiState.Loading)
    val uiState: StateFlow<AddPurchaseOrderUiState> = _uiState.asStateFlow()

    val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    val branchId: Long = savedStateHandle.get<String>("selectedSedeId")?.toLongOrNull() ?: 1L

    var selectedProvider by mutableStateOf<ProviderResource?>(null)
    var selectedSupplyItem by mutableStateOf<SupplyItemResource?>(null)
    var quantity by mutableStateOf("")
    var unitPrice by mutableStateOf("")
    var purchaseDate by mutableStateOf(LocalDate.now()) // Por defecto, la fecha actual
    var expirationDate by mutableStateOf<LocalDate?>(null) // Opcional
    var notes by mutableStateOf("")

    private val _availableProviders = MutableStateFlow<List<ProviderResource>>(emptyList())
    val availableProviders: StateFlow<List<ProviderResource>> = _availableProviders.asStateFlow()

    private val _availableSupplyItems = MutableStateFlow<List<SupplyItemResource>>(emptyList())
    val availableSupplyItems: StateFlow<List<SupplyItemResource>> = _availableSupplyItems.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = AddPurchaseOrderUiState.Loading
            try {
                // Cargar proveedores disponibles
                val providersResponse = RetrofitClient.contactsApi.getProviders(portfolioId)
                if (providersResponse.isSuccessful && providersResponse.body() != null) {
                    _availableProviders.value = providersResponse.body()!!
                } else {
                    _uiState.value = AddPurchaseOrderUiState.Error("Error al cargar proveedores: ${providersResponse.errorBody()?.string()}")
                    return@launch
                }

                // Cargar insumos disponibles para la sede actual
                val supplyItemsResponse = RetrofitClient.productApi.getSupplyItemsByBranch(branchId) // Usamos productApi para obtener los supply-items por rama
                if (supplyItemsResponse.isSuccessful && supplyItemsResponse.body() != null) {
                    _availableSupplyItems.value = supplyItemsResponse.body()!!
                } else {
                    _uiState.value = AddPurchaseOrderUiState.Error("Error al cargar insumos: ${supplyItemsResponse.errorBody()?.string()}")
                    return@launch
                }
                _uiState.value = AddPurchaseOrderUiState.ReadyForInput
            } catch (e: Exception) {
                _uiState.value = AddPurchaseOrderUiState.Error("Error de conexión al cargar datos: ${e.message}")
            }
        }
    }

    // Registra la orden de compra y el movimiento de inventario asociado
    fun registerPurchaseOrder() {
        if (selectedProvider == null || selectedSupplyItem == null || quantity.isBlank() || unitPrice.isBlank()) {
            _uiState.value = AddPurchaseOrderUiState.Error("Todos los campos con * son obligatorios.")
            return
        }

        _uiState.value = AddPurchaseOrderUiState.Loading
        viewModelScope.launch {
            try {
                val request = CreatePurchaseOrderRequest(
                    branchId = branchId,
                    providerId = selectedProvider!!.id,
                    supplyItemId = selectedSupplyItem!!.id,
                    quantity = quantity.toDoubleOrNull() ?: 0.0,
                    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                    purchaseDate = purchaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    expirationDate = expirationDate?.format(DateTimeFormatter.ISO_LOCAL_DATE), // Formatear solo si no es nulo
                    notes = notes.ifBlank { null }
                )
                val response = RetrofitClient.purchaseOrdersApi.createPurchaseOrder(request)

                if (response.isSuccessful && response.body() != null) {
                    val purchaseOrderResource = response.body()!!
                    // Registrar movimiento de inventario (ENTRADA)
                    val transaction = CreateInventoryTransactionResource(
                        supplyItemId = purchaseOrderResource.supplyItemId,
                        branchId = purchaseOrderResource.branchId,
                        type = TransactionType.ENTRADA, // Es una entrada de inventario
                        quantity = purchaseOrderResource.quantity,
                        origin = "Compra de Insumo '${purchaseOrderResource.supplyItemName}' (Orden ID: ${purchaseOrderResource.id})"
                    )
                    RetrofitClient.inventoryApi.registerMovement(transaction)

                    _uiState.value = AddPurchaseOrderUiState.Success("Orden de compra registrada exitosamente. ID: ${purchaseOrderResource.id}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al registrar orden de compra."
                    println("ERROR AL REGISTRAR COMPRA: Código ${response.code()} - Mensaje: $errorBody") // Agregado para depuración
                    _uiState.value = AddPurchaseOrderUiState.Error("Error al registrar orden de compra: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                println("ERROR DE CONEXIÓN AL REGISTRAR COMPRA: ${e.message}") // Agregado para depuración
                _uiState.value = AddPurchaseOrderUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Factory para crear AddPurchaseOrderViewModel
    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AddPurchaseOrderViewModel::class.java)) {
                        return AddPurchaseOrderViewModel(
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