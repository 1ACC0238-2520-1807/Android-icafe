package com.example.icafe.features.finances.presentation.sales

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.finances.data.network.SaleResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import android.util.Log

sealed class SaleDetailUiState {
    object Loading : SaleDetailUiState()
    data class Success(val sale: SaleResource) : SaleDetailUiState()
    data class Error(val message: String) : SaleDetailUiState()
}

class SaleDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val saleId: Long = savedStateHandle.get<Long>("saleId") ?: -1L
    private val branchId: Long = savedStateHandle.get<String>("selectedSedeId")?.toLongOrNull() ?: -1L
    private val portfolioId: String = savedStateHandle.get<String>("portfolioId") ?: ""

    private val _uiState = MutableStateFlow<SaleDetailUiState>(SaleDetailUiState.Loading)
    val uiState: StateFlow<SaleDetailUiState> = _uiState.asStateFlow()

    init {
        if (saleId != -1L) {
            loadSaleDetails(saleId)
        } else {
            _uiState.value = SaleDetailUiState.Error("ID de venta no válido.")
        }
    }

    // ¡CAMBIO AQUÍ! Eliminar la palabra clave 'private'
    fun loadSaleDetails(id: Long) { // Ahora es una función pública
        viewModelScope.launch {
            _uiState.value = SaleDetailUiState.Loading
            try {
                val response = RetrofitClient.salesApi.getSaleById(id)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = SaleDetailUiState.Success(response.body()!!)
                    Log.d("SaleDetailVM", "Venta cargada: ${response.body()!!}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar el detalle de la venta."
                    _uiState.value = SaleDetailUiState.Error("Error: ${response.code()} - $errorBody")
                    Log.e("SaleDetailVM", "Error al cargar venta ${id}: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = SaleDetailUiState.Error("Error de conexión: ${e.message}")
                Log.e("SaleDetailVM", "Excepción al cargar venta ${id}: ${e.message}", e)
            }
        }
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String, saleId: Long): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SaleDetailViewModel::class.java)) {
                        return SaleDetailViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("portfolioId", portfolioId)
                                set("selectedSedeId", selectedSedeId)
                                set("saleId", saleId)
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}