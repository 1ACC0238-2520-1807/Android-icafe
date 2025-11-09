package com.example.icafe.features.inventory.presentation.item

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

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!! // MODIFIED: Extract portfolioId
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L // Convert to Long

    init {
        loadItems()
    }

    fun loadItems() {
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            try {
                // Fetch ALL supply items (no GET by branch endpoint available)
                val response = RetrofitClient.productApi.getAllSupplyItems() // Returns Response<List<SupplyItemResource>>
                if (response.isSuccessful && response.body() != null) {
                    val allSupplyItems = response.body()!! // Correctly declares List<SupplyItemResource>
                    // Filter supply items client-side by selectedSedeId (branchId)
                    // 'it' correctly refers to a SupplyItemResource here.
                    val filteredSupplyItems = allSupplyItems.filter { it.branchId == branchId }
                    _uiState.value = ItemListUiState.Success(filteredSupplyItems)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al cargar insumos."
                    _uiState.value = ItemListUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = ItemListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

// DEFINITION OF THE FACTORY - AT THE END OF THE SAME VIEWMODEL FILE
class ItemListViewModelFactory(
    private val portfolioId: String, // MODIFIED: Add portfolioId
    private val selectedSedeId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemListViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId) // MODIFIED: Set portfolioId
                    set("selectedSedeId", selectedSedeId)
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}