package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.finances.data.network.PurchaseOrderResource
import kotlinx.coroutines.launch
import java.lang.Exception

// Estados posibles de la UI para la lista de órdenes de compra
sealed class PurchaseOrderListUiState {
    object Loading : PurchaseOrderListUiState()
    data class Success(val purchaseOrders: List<PurchaseOrderResource>) : PurchaseOrderListUiState()
    data class Error(val message: String) : PurchaseOrderListUiState()
}

class PurchaseOrderListViewModel(private val branchId: Long) : ViewModel() {
    // Usamos mutableStateOf para que el UI pueda observar cambios en el estado
    var uiState by mutableStateOf<PurchaseOrderListUiState>(PurchaseOrderListUiState.Loading)
        private set

    init {
        loadPurchaseOrders()
    }

    // Función para cargar las órdenes de compra desde la API
    fun loadPurchaseOrders() {
        uiState = PurchaseOrderListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.purchaseOrdersApi.getPurchaseOrdersByBranchId(branchId)
                if (response.isSuccessful && response.body() != null) {
                    uiState = PurchaseOrderListUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar órdenes de compra."
                    uiState = PurchaseOrderListUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                // Captura errores de red u otros problemas
                uiState = PurchaseOrderListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Factory para crear una instancia de PurchaseOrderListViewModel con dependencias
    companion object {
        fun Factory(branchId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PurchaseOrderListViewModel::class.java)) {
                        return PurchaseOrderListViewModel(branchId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}