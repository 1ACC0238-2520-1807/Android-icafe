package com.example.icafe.features.inventory.presentation.item

import android.util.Log // Asegúrate de tener esta importación
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ItemListUiState {
    object Loading : ItemListUiState()
    data class Success(val items: List<SupplyItemResource>) : ItemListUiState()
    data class Error(val message: String) : ItemListUiState()
}

class ItemListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<ItemListUiState>(ItemListUiState.Loading)
    val uiState: StateFlow<ItemListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    // Asegúrate de que este branchId sea el correcto para el filtrado
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L

    init {
        loadItems()
    }

    fun loadItems() {
        Log.d("ItemListViewModel", "loadItems() llamado. branchId de la sesión (derivado de selectedSedeId '$selectedSedeId'): $branchId")
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getAllSupplyItems()
                if (response.isSuccessful && response.body() != null) {
                    val allSupplyItems = response.body()!!
                    Log.d("ItemListViewModel", "API regresó ${allSupplyItems.size} insumos ANTES de filtrar.")

                    if (allSupplyItems.isNotEmpty()) {
                        allSupplyItems.forEach {
                            Log.d("ItemListViewModel", "  - Item recibido de API: ID=${it.id}, Nombre='${it.name}', BranchID_API=${it.branchId}")
                        }
                    } else {
                        Log.d("ItemListViewModel", "  - No se recibieron insumos de la API.")
                    }

                    // Filtrado por branchId en el frontend
                    val filteredSupplyItems = allSupplyItems.filter { it.branchId == branchId }
                    Log.d("ItemListViewModel", "Después de filtrar por branchId de sesión ($branchId), se muestran ${filteredSupplyItems.size} insumos.")
                    if (filteredSupplyItems.isNotEmpty()) {
                        filteredSupplyItems.forEach {
                            Log.d("ItemListViewModel", "  - Item FILTRADO Y MOSTRADO: ID=${it.id}, Nombre='${it.name}', BranchID_API=${it.branchId}")
                        }
                    } else {
                        Log.d("ItemListViewModel", "  - Ningún insumo coincide con el branchId de sesión ($branchId) después de filtrar.")
                    }


                    _uiState.value = ItemListUiState.Success(filteredSupplyItems)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido al cargar insumos."
                    Log.e("ItemListViewModel", "Error al cargar insumos de la API: ${response.code()} - $errorBody")
                    _uiState.value = ItemListUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                Log.e("ItemListViewModel", "Excepción de red al cargar insumos: ${e.message}", e)
                _uiState.value = ItemListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // DEFINITION OF THE FACTORY - INTEGRATED
    companion object {
        fun ItemListViewModelFactory(portfolioId: String, selectedSedeId: String): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
                        return ItemListViewModel(
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