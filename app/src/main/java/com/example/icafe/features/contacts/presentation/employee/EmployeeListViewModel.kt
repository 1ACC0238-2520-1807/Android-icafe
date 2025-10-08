package com.example.icafe.features.contacts.presentation.employee

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.EmployeeResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EmployeeListUiState {
    object Loading : EmployeeListUiState()
    data class Success(val employees: List<EmployeeResource>) : EmployeeListUiState()
    data class Error(val message: String) : EmployeeListUiState()
}

class EmployeeListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<EmployeeListUiState>(EmployeeListUiState.Loading)
    val uiState: StateFlow<EmployeeListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        _uiState.value = EmployeeListUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.contactsApi.getEmployees(portfolioId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = EmployeeListUiState.Success(response.body()!!)
                } else {
                    _uiState.value = EmployeeListUiState.Error("Error al cargar empleados.")
                }
            } catch (e: Exception) {
                _uiState.value = EmployeeListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}