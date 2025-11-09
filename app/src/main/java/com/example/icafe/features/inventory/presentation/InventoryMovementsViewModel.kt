package com.example.icafe.features.inventory.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.InventoryTransactionResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class InventoryMovementsUiState {
    object FeatureUnavailable : InventoryMovementsUiState() // NEW STATE
    object Loading : InventoryMovementsUiState()
    data class Success(val movements: List<InventoryTransactionResource>) : InventoryMovementsUiState()
    data class Error(val message: String) : InventoryMovementsUiState()
}

class InventoryMovementsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<InventoryMovementsUiState>(InventoryMovementsUiState.FeatureUnavailable) // Start in unavailable state
    val uiState: StateFlow<InventoryMovementsUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!! // MODIFIED: Extract portfolioId
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L // Convert to Long

    init {
        // No auto-load needed as feature is unavailable from backend.
        _uiState.value = InventoryMovementsUiState.FeatureUnavailable
    }

    // This function remains, but it will now indicate unavailability or handle a single POST
    fun loadMovements() {
        _uiState.value = InventoryMovementsUiState.FeatureUnavailable // Explicitly set to unavailable
        // If you had a way to retrieve some mocked data or allow single POST, you'd put it here.
        // Example if you later add a local DB or mock service:
        /*
        _uiState.value = InventoryMovementsUiState.Loading
        viewModelScope.launch {
            try {
                // If backend provides a specific movements endpoint, use it here
                // For now, assuming it's unavailable or you would mock it.
                // val response = RetrofitClient.inventoryApi.getAllMovementsByBranch(branchId)
                // if (response.isSuccessful && response.body() != null) {
                //     _uiState.value = InventoryMovementsUiState.Success(response.body()!!)
                // } else {
                //     val errorBody = response.errorBody()?.string() ?: "Error al cargar movimientos de inventario."
                //     _uiState.value = InventoryMovementsUiState.Error(errorBody)
                // }
                 _uiState.value = InventoryMovementsUiState.FeatureUnavailable // No actual backend call
            } catch (e: Exception) {
                 _uiState.value = InventoryMovementsUiState.Error("Error de conexión: ${e.message}")
            }
        }
        */
    }
}

// DEFINITION OF THE FACTORY - AT THE END OF THE SAME VIEWMODEL FILE
class InventoryMovementsViewModelFactory(
    private val portfolioId: String, // MODIFIED: Add portfolioId
    private val selectedSedeId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryMovementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryMovementsViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId) // MODIFIED: Set portfolioId
                    set("selectedSedeId", selectedSedeId)
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}