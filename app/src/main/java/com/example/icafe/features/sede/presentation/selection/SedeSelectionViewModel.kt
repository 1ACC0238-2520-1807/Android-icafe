package com.example.icafe.features.sede.presentation.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.BranchResource
import com.example.icafe.core.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SedeSelectionUiState {
    object Loading : SedeSelectionUiState()
    data class Success(val branches: List<BranchResource>) : SedeSelectionUiState()
    data class Error(val message: String) : SedeSelectionUiState()
}

class SedeSelectionViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow<SedeSelectionUiState>(SedeSelectionUiState.Loading)
    val uiState: StateFlow<SedeSelectionUiState> = _uiState

    val sedes: StateFlow<List<BranchResource>> = _uiState.asStateFlow().let { flow ->
        MutableStateFlow(emptyList<BranchResource>()).also { resultFlow ->
            viewModelScope.launch {
                flow.collect { state ->
                    if (state is SedeSelectionUiState.Success) {
                        resultFlow.value = state.branches
                    } else {
                        resultFlow.value = emptyList()
                    }
                }
            }
        }
    }

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!! // This is actually ownerId

    init {
        loadSedes()
    }

    fun loadSedes() {
        _uiState.value = SedeSelectionUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.branchApi.getBranchesByOwnerId(portfolioId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = SedeSelectionUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al cargar sedes."
                    _uiState.value = SedeSelectionUiState.Error(errorBody)
                }
            } catch (e: Exception) {
                _uiState.value = SedeSelectionUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteSede(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.branchApi.deleteBranch(id)
                if (response.isSuccessful) {
                    loadSedes() // Refresh the list after deletion
                } else {
                    // Consider emitting an error event or showing a Snackbar
                }
            } catch (e: Exception) {
                // Consider emitting an error event or showing a Snackbar
            }
        }
    }
}

// DEFINITION OF THE FACTORY - AT THE END OF THE SAME VIEWMODEL FILE
class SedeSelectionViewModelFactory(
    private val portfolioId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SedeSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SedeSelectionViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId)
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}