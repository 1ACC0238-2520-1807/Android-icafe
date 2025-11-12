package com.example.icafe.features.finances.presentation.sales

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.finances.data.network.SaleResource
import kotlinx.coroutines.launch
import java.lang.Exception


sealed class SalesListUiState {
    object Loading : SalesListUiState()
    data class Success(val sales: List<SaleResource>) : SalesListUiState()
    data class Error(val message: String) : SalesListUiState()
}

class SalesListViewModel(private val branchId: Long) : ViewModel() {
    // Usamos mutableStateOf para que el UI pueda observar cambios en el estado
    var uiState by mutableStateOf<SalesListUiState>(SalesListUiState.Loading)
        private set

    init {
        loadSales()
    }

    // Función para cargar las ventas desde la API
    fun loadSales() {
        uiState = SalesListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.salesApi.getSalesByBranchId(branchId)
                if (response.isSuccessful && response.body() != null) {
                    uiState = SalesListUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar ventas."
                    uiState = SalesListUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                // Captura errores de red u otros problemas
                uiState = SalesListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // Factory para crear una instancia de SalesListViewModel con dependencias
    companion object {
        fun Factory(branchId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SalesListViewModel::class.java)) {
                        return SalesListViewModel(branchId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}