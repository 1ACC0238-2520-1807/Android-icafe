package com.example.icafe.features.contacts.presentation.employee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.EmployeeRequest
import com.example.icafe.features.contacts.data.network.EmployeeResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Eventos para manejar la navegación o mostrar SnackBar
sealed class EmployeeEvent {
    object ActionSuccess : EmployeeEvent()
    data class ActionError(val message: String) : EmployeeEvent()
}

class EmployeeDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    // --- ESTADO DE LOS CAMPOS DEL FORMULARIO ---
    var name by mutableStateOf("")
    var role by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var salary by mutableStateOf("")
    var branchId by mutableStateOf("1") // Placeholder, puedes cambiarlo

    // --- ESTADO DE LA UI ---
    var isLoading by mutableStateOf(false)
        private set
    var employee by mutableStateOf<EmployeeResource?>(null)
        private set

    private val _events = MutableSharedFlow<EmployeeEvent>()
    val events = _events.asSharedFlow()

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val employeeId: String? = savedStateHandle.get<String>("employeeId")

    init {
        employeeId?.let {
            loadEmployee(it.toLong())
        }
    }

    private fun loadEmployee(id: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.contactsApi.getEmployeeById(portfolioId, id)
                if (response.isSuccessful && response.body() != null) {
                    employee = response.body()
                    // Pre-llenar campos para edición
                    name = employee?.name ?: ""
                    role = employee?.role ?: ""
                    email = employee?.email ?: ""
                    phoneNumber = employee?.phoneNumber ?: ""
                    salary = employee?.salary ?: ""
                    branchId = employee?.branchId?.toString() ?: "1"
                } else {
                    _events.emit(EmployeeEvent.ActionError("No se pudo cargar el empleado."))
                }
            } catch (e: Exception) {
                _events.emit(EmployeeEvent.ActionError("Error de conexión."))
            } finally {
                isLoading = false
            }
        }
    }

    fun saveEmployee() {
        isLoading = true
        viewModelScope.launch {
            try {
                val request = EmployeeRequest(
                    name = name,
                    role = role,
                    email = email,
                    phoneNumber = phoneNumber,
                    salary = salary,
                    branchId = branchId.toLongOrNull() ?: 1L
                )

                val response = if (employeeId == null) {
                    RetrofitClient.contactsApi.addEmployee(portfolioId, request)
                } else {
                    RetrofitClient.contactsApi.updateEmployee(portfolioId, employeeId.toLong(), request)
                }

                if (response.isSuccessful) {
                    _events.emit(EmployeeEvent.ActionSuccess)
                } else {
                    _events.emit(EmployeeEvent.ActionError("Error al guardar."))
                }
            } catch (e: Exception) {
                _events.emit(EmployeeEvent.ActionError("Error de conexión."))
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteEmployee() {
        isLoading = true
        employeeId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.contactsApi.deleteEmployee(portfolioId, id.toLong())
                    if (response.isSuccessful) {
                        _events.emit(EmployeeEvent.ActionSuccess)
                    } else {
                        _events.emit(EmployeeEvent.ActionError("Error al eliminar."))
                    }
                } catch (e: Exception) {
                    _events.emit(EmployeeEvent.ActionError("Error de conexión."))
                } finally {
                    isLoading = false
                }
            }
        }
    }
}