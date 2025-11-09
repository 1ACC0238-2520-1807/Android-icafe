package com.example.icafe.features.sede.presentation.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.BranchResource
import com.example.icafe.core.data.network.CreateBranchRequest
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.core.data.network.UpdateBranchRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AddEditSedeEvent {
    object ActionSuccess : AddEditSedeEvent()
    data class ActionError(val message: String) : AddEditSedeEvent()
}

class AddEditSedeViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    var name by mutableStateOf("")
    var address by mutableStateOf("")

    private val _events = MutableSharedFlow<AddEditSedeEvent>()
    val events = _events.asSharedFlow()

    private val sedeId: String? = savedStateHandle.get<String>("sedeId")
    private val ownerId: String? = savedStateHandle.get<String>("portfolioId")

    private var currentSede: BranchResource? = null

    init {
        if (sedeId != "new" && sedeId != null) {
            loadSedeForEdit(sedeId)
        }
    }

    private fun loadSedeForEdit(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.branchApi.getBranchById(id)
                if (response.isSuccessful && response.body() != null) {
                    currentSede = response.body()
                    name = currentSede?.name ?: ""
                    address = currentSede?.address ?: ""
                } else {
                    _events.emit(AddEditSedeEvent.ActionError("Sede no encontrada o error al cargar."))
                }
            } catch (e: Exception) {
                _events.emit(AddEditSedeEvent.ActionError("Error de conexión al cargar la sede."))
            }
        }
    }

    fun saveSede() {
        if (name.isBlank() || address.isBlank()) {
            viewModelScope.launch { _events.emit(AddEditSedeEvent.ActionError("Nombre y dirección no pueden estar vacíos.")) }
            return
        }

        viewModelScope.launch {
            try {
                if (sedeId == "new" || sedeId == null) {
                    if (ownerId == null) {
                        _events.emit(AddEditSedeEvent.ActionError("No se pudo obtener el ID del propietario para crear la sede."))
                        return@launch
                    }
                    val request = CreateBranchRequest(
                        ownerId = ownerId.toLong(),
                        name = name,
                        address = address
                    )
                    val response = RetrofitClient.branchApi.createBranch(request)
                    if (response.isSuccessful) {
                        _events.emit(AddEditSedeEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido al crear la sede."
                        _events.emit(AddEditSedeEvent.ActionError(errorBody))
                    }
                } else {
                    val request = UpdateBranchRequest(
                        name = name,
                        address = address
                    )
                    val response = RetrofitClient.branchApi.updateBranch(sedeId, request)
                    if (response.isSuccessful) {
                        _events.emit(AddEditSedeEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido al actualizar la sede."
                        _events.emit(AddEditSedeEvent.ActionError(errorBody))
                    }
                }
            } catch (e: Exception) {
                _events.emit(AddEditSedeEvent.ActionError("Error de conexión al guardar la sede: ${e.message}"))
            }
        }
    }

    // DEFINITION OF THE FACTORY - INTEGRATED
    companion object {
        fun Factory(sedeId: String?, portfolioId: String?): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AddEditSedeViewModel::class.java)) {
                        return AddEditSedeViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("sedeId", sedeId)
                                set("portfolioId", portfolioId)
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}