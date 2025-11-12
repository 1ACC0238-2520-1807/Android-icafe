package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.finances.data.network.PurchaseOrderResource
import com.example.icafe.features.products.data.network.ProductApiService // Importar ProductApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import android.util.Log

// Estados posibles de la UI para el detalle de la orden de compra
sealed class PurchaseOrderDetailUiState {
    object Loading : PurchaseOrderDetailUiState()
    // Ahora contiene la PurchaseOrderResource completa y el nombre del insumo resuelto
    data class Success(
        val purchaseOrder: PurchaseOrderResource,
        val resolvedSupplyItemName: String // Para almacenar el nombre resuelto
    ) : PurchaseOrderDetailUiState()
    data class Error(val message: String) : PurchaseOrderDetailUiState()
}

class PurchaseOrderDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val purchaseOrderId: Long = savedStateHandle.get<Long>("purchaseOrderId") ?: -1L
    private val branchId: Long = savedStateHandle.get<String>("selectedSedeId")?.toLongOrNull() ?: -1L
    private val portfolioId: String = savedStateHandle.get<String>("portfolioId") ?: ""

    private val _uiState = MutableStateFlow<PurchaseOrderDetailUiState>(PurchaseOrderDetailUiState.Loading)
    val uiState: StateFlow<PurchaseOrderDetailUiState> = _uiState.asStateFlow()

    init {
        if (purchaseOrderId != -1L && branchId != -1L) {
            loadPurchaseOrderDetails(purchaseOrderId, branchId)
        } else {
            _uiState.value = PurchaseOrderDetailUiState.Error("ID de orden de compra o ID de sucursal no válido.")
        }
    }

    fun loadPurchaseOrderDetails(id: Long, branchId: Long) {
        viewModelScope.launch {
            _uiState.value = PurchaseOrderDetailUiState.Loading
            try {
                val response = RetrofitClient.purchaseOrdersApi.getPurchaseOrderById(id, branchId)
                if (response.isSuccessful && response.body() != null) {
                    val purchaseOrder = response.body()!!
                    var resolvedName: String? = purchaseOrder.supplyItemName // Empezar con el nombre de la orden de compra

                    // Si el nombre del insumo de PurchaseOrderResource es nulo o vacío, intentar buscarlo por ID
                    if (resolvedName.isNullOrBlank()) {
                        try {
                            val supplyItemResponse = RetrofitClient.productApi.getSupplyItemById(purchaseOrder.supplyItemId)
                            if (supplyItemResponse.isSuccessful && supplyItemResponse.body() != null) {
                                resolvedName = supplyItemResponse.body()!!.name
                                Log.d("PurchaseDetailVM", "Nombre de insumo obtenido por ID: $resolvedName")
                            } else {
                                Log.w("PurchaseDetailVM", "No se pudieron obtener los detalles del insumo para ID ${purchaseOrder.supplyItemId}: ${supplyItemResponse.code()} - ${supplyItemResponse.errorBody()?.string()}")
                            }
                        } catch (e: Exception) {
                            Log.e("PurchaseDetailVM", "Excepción al obtener detalles del insumo para ID ${purchaseOrder.supplyItemId}: ${e.message}", e)
                        }
                    }

                    _uiState.value = PurchaseOrderDetailUiState.Success(
                        purchaseOrder,
                        resolvedName ?: "Desconocido" // Fallback si el nombre sigue siendo nulo
                    )
                    Log.d("PurchaseDetailVM", "Orden de compra cargada: $purchaseOrder, Insumo resuelto: ${resolvedName}")

                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar el detalle de la orden de compra."
                    _uiState.value = PurchaseOrderDetailUiState.Error("Error: ${response.code()} - $errorBody")
                    Log.e("PurchaseDetailVM", "Error al cargar orden de compra ${id}: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = PurchaseOrderDetailUiState.Error("Error de conexión: ${e.message}")
                Log.e("PurchaseDetailVM", "Excepción al cargar orden de compra ${id}: ${e.message}", e)
            }
        }
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String, purchaseOrderId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PurchaseOrderDetailViewModel::class.java)) {
                        return PurchaseOrderDetailViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("portfolioId", portfolioId)
                                set("selectedSedeId", selectedSedeId)
                                set("purchaseOrderId", purchaseOrderId)
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}