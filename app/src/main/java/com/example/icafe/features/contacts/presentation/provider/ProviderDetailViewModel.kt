package com.example.icafe.features.contacts.presentation.provider

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.ProviderRequest
import com.example.icafe.features.contacts.data.network.ProviderResource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class ProviderEvent {
    object ActionSuccess : ProviderEvent()
    data class ActionError(val message: String) : ProviderEvent()
}

class ProviderDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    var nameCompany by mutableStateOf("")
    var ruc by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set
    var provider by mutableStateOf<ProviderResource?>(null)
        private set

    private val _events = MutableSharedFlow<ProviderEvent>()
    val events = _events.asSharedFlow()

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!! // Kept for consistency, not directly used by Providers
    private val providerId: String? = savedStateHandle.get<String>("providerId")

    init {
        providerId?.let {
            loadProvider(it.toLong())
        }
    }

    private fun loadProvider(id: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.contactsApi.getProviderById(portfolioId, id)
                if (response.isSuccessful && response.body() != null) {
                    provider = response.body()
                    nameCompany = provider?.nameCompany ?: ""
                    ruc = provider?.ruc ?: ""
                    email = provider?.email ?: ""
                    phoneNumber = provider?.phoneNumber ?: ""
                } else {
                    _events.emit(ProviderEvent.ActionError("No se pudo cargar el proveedor."))
                }
            } catch (e: Exception) {
                _events.emit(ProviderEvent.ActionError("Error de conexión."))
            } finally {
                isLoading = false
            }
        }
    }

    fun saveProvider() {
        isLoading = true
        viewModelScope.launch {
            try {
                val request = ProviderRequest(
                    nameCompany = nameCompany,
                    ruc = ruc,
                    email = email,
                    phoneNumber = phoneNumber
                )

                val response = if (providerId == null) {
                    RetrofitClient.contactsApi.addProvider(portfolioId, request)
                } else {
                    RetrofitClient.contactsApi.updateProvider(portfolioId, providerId.toLong(), request)
                }

                if (response.isSuccessful) {
                    _events.emit(ProviderEvent.ActionSuccess)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error al guardar."
                    _events.emit(ProviderEvent.ActionError("Error del servidor: $errorBody"))
                }
            } catch (e: Exception) {
                _events.emit(ProviderEvent.ActionError("Error de conexión: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteProvider() {
        isLoading = true
        providerId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.contactsApi.deleteProvider(portfolioId, id.toLong())
                    if (response.isSuccessful) {
                        _events.emit(ProviderEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error al eliminar."
                        _events.emit(ProviderEvent.ActionError("Error del servidor: $errorBody"))
                    }
                } catch (e: Exception) {
                    _events.emit(ProviderEvent.ActionError("Error de conexión: ${e.message}"))
                } finally {
                    isLoading = false
                }
            }
        }
    }
}

// DEFINICIÓN DE LA FACTORY - DENTRO DEL MISMO ARCHIVO DEL ViewModel
class ProviderDetailViewModelFactory(
    private val portfolioId: String,
    private val selectedSedeId: String,
    private val providerId: Long?
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProviderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProviderDetailViewModel(
                savedStateHandle = SavedStateHandle().apply {
                    set("portfolioId", portfolioId)
                    set("selectedSedeId", selectedSedeId)
                    set("providerId", providerId?.toString())
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}