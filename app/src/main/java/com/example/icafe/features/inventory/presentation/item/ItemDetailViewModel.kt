package com.example.icafe.features.inventory.presentation.item

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.ItemRequest
import com.example.icafe.features.inventory.data.network.ItemResource
import com.example.icafe.features.inventory.data.network.UnitMeasureType
import com.example.icafe.features.inventory.data.network.UpdateItemRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class ItemEvent {
    object ActionSuccess : ItemEvent()
    data class ActionError(val message: String) : ItemEvent()
}

class ItemDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    var name by mutableStateOf("")
    var unit by mutableStateOf(UnitMeasureType.UNIDADES)
    var initialQuantity by mutableStateOf("")
    var reorderPoint by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set
    var item by mutableStateOf<ItemResource?>(null)
        private set

    private val _events = MutableSharedFlow<ItemEvent>()
    val events = _events.asSharedFlow()

    private val itemId: String? = savedStateHandle.get<String>("itemId")

    init {
        itemId?.let {
            loadItem(it.toLong())
        }
    }

    private fun loadItem(id: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.inventoryApi.getItemById(id)
                if (response.isSuccessful && response.body() != null) {
                    item = response.body()
                    name = item?.nombre ?: ""
                    unit = item?.unidadMedida ?: UnitMeasureType.UNIDADES
                    initialQuantity = item?.cantidadActual?.toString() ?: ""
                    reorderPoint = item?.puntoDeReorden?.toString() ?: ""
                } else {
                    _events.emit(ItemEvent.ActionError("No se pudo cargar el insumo."))
                }
            } catch (e: Exception) {
                _events.emit(ItemEvent.ActionError("Error de conexión."))
            } finally {
                isLoading = false
            }
        }
    }

    fun saveItem() {
        // Validación en el frontend
        val quantityValue = initialQuantity.toDoubleOrNull()
        val reorderValue = reorderPoint.toDoubleOrNull()

        if (name.isBlank()) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("El nombre no puede estar vacío.")) }
            return
        }
        if (reorderValue == null || reorderValue < 0) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("El punto de reorden debe ser un número válido.")) }
            return
        }
        if (itemId == null && (quantityValue == null || quantityValue <= 0)) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("La cantidad inicial debe ser un número mayor a cero.")) }
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                if (itemId == null) {
                    // --- MODO CREACIÓN ---
                    val request = ItemRequest(
                        nombre = name,
                        unidadMedida = unit,
                        cantidadInicial = quantityValue!!,
                        puntoDeReorden = reorderValue,
                        supplyManagementId = 1L // <-- ¡AQUÍ ESTÁ LA CLAVE! Usamos el ID que creaste.
                    )

                    Log.d("ItemViewModel", "Enviando para crear: ${Gson().toJson(request)}")

                    val response = RetrofitClient.inventoryApi.addItem(request)

                    if (response.isSuccessful) {
                        Log.d("ItemViewModel", "Creación exitosa: ${response.body()}")
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        Log.e("ItemViewModel", "Error al crear: ${response.code()} - $errorBody")
                        _events.emit(ItemEvent.ActionError("Error del servidor: $errorBody"))
                    }
                } else {
                    // --- MODO EDICIÓN ---
                    val request = UpdateItemRequest(
                        nombre = name,
                        unidadMedida = unit,
                        puntoDeReorden = reorderValue
                    )
                    Log.d("ItemViewModel", "Enviando para actualizar (ID: $itemId): ${Gson().toJson(request)}")
                    val response = RetrofitClient.inventoryApi.updateItem(itemId.toLong(), request)
                    if (response.isSuccessful) {
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        _events.emit(ItemEvent.ActionError("Error del servidor: $errorBody"))
                    }
                }
            } catch (e: Exception) {
                Log.e("ItemViewModel", "Excepción de red: ${e.message}", e)
                _events.emit(ItemEvent.ActionError("Error de conexión. Revisa el Logcat."))
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteItem() {
        isLoading = true
        itemId?.let { id ->
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.inventoryApi.deleteItem(id.toLong())
                    if (response.isSuccessful) {
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        _events.emit(ItemEvent.ActionError("Error al eliminar."))
                    }
                } catch (e: Exception) {
                    _events.emit(ItemEvent.ActionError("Error de conexión."))
                } finally {
                    isLoading = false
                }
            }
        }
    }
}
