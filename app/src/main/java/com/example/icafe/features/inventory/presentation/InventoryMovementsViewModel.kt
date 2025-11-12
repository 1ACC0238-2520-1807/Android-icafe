package com.example.icafe.features.inventory.presentation

import android.util.Log // Importar Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.InventoryTransactionResource
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

sealed class InventoryMovementsUiState {
    object Loading : InventoryMovementsUiState()
    data class Success(
        val movements: List<InventoryTransactionResource>,
        val selectedMovement: InventoryTransactionResource?,
        val supplyItemDetails: Map<Long, SupplyItemResource>
    ) : InventoryMovementsUiState()
    data class Error(val message: String) : InventoryMovementsUiState()
}

class InventoryMovementsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _movements = MutableStateFlow<List<InventoryTransactionResource>>(emptyList())
    private val _selectedMovement = MutableStateFlow<InventoryTransactionResource?>(null)
    private val _supplyItemDetails = MutableStateFlow<MutableMap<Long, SupplyItemResource>>(ConcurrentHashMap())

    val uiState: StateFlow<InventoryMovementsUiState> = combine(
        _isLoading,
        _errorMessage,
        _movements,
        _selectedMovement,
        _supplyItemDetails
    ) { isLoading, errorMessage, movements, selectedMovement, supplyItemDetails ->
        when {
            isLoading -> InventoryMovementsUiState.Loading
            errorMessage != null -> InventoryMovementsUiState.Error(errorMessage)
            else -> InventoryMovementsUiState.Success(movements, selectedMovement, supplyItemDetails)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryMovementsUiState.Loading
    )

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L

    init {
        loadMovements()
    }

    fun loadMovements() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val movementsResponse = RetrofitClient.inventoryApi.getAllStockMovementsByBranch(branchId)
                if (movementsResponse.isSuccessful && movementsResponse.body() != null) {
                    val allLoadedMovements = movementsResponse.body()!!

                    // --- Lógica de filtrado ELIMINADA para mostrar todos los movimientos ---
                    // Conservamos el ordenamiento si lo deseas para una mejor visualización
                    val movementsToDisplay = allLoadedMovements.sortedByDescending { it.movementDate }

                    _movements.value = movementsToDisplay

                    val allSupplyItemsResponse = RetrofitClient.productApi.getSupplyItemsByBranch(branchId)
                    if (allSupplyItemsResponse.isSuccessful && allSupplyItemsResponse.body() != null) {
                        val detailsMap = ConcurrentHashMap<Long, SupplyItemResource>()
                        allSupplyItemsResponse.body()!!.forEach { item ->
                            detailsMap[item.id] = item
                        }
                        _supplyItemDetails.value = detailsMap
                    } else {
                        Log.e("InventoryVM", "Failed to load all supply items for branch: ${allSupplyItemsResponse.errorBody()?.string()}")
                    }

                    if (movementsToDisplay.isNotEmpty()) {
                        _selectedMovement.value = movementsToDisplay.first()
                    } else {
                        _selectedMovement.value = null // No hay movimientos para seleccionar
                    }
                } else {
                    val errorBody = movementsResponse.errorBody()?.string() ?: "Error desconocido al cargar movimientos de inventario."
                    _errorMessage.value = errorBody
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectMovement(movement: InventoryTransactionResource) {
        _selectedMovement.value = movement
    }
}

class InventoryMovementsViewModelFactory(
    private val portfolioId: String,
    private val selectedSedeId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryMovementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryMovementsViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId)
                    set("selectedSedeId", selectedSedeId)
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}