package com.example.icafe.features.inventory.presentation.item

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.CreateSupplyItemRequest
import com.example.icafe.features.inventory.data.network.SupplyItemResource // Ahora se llama InventoryDataModels.SupplyItemResource
import com.example.icafe.features.inventory.data.network.UnitMeasureType // Ahora se llama InventoryDataModels.UnitMeasureType
import com.example.icafe.features.inventory.data.network.UpdateSupplyItemRequest // Ahora se llama InventoryDataModels.UpdateSupplyItemRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate // NEW import
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter // NEW import
import java.time.format.DateTimeParseException // NEW import
import com.example.icafe.features.contacts.data.network.ProviderResource

sealed class ItemEvent {
    object ActionSuccess : ItemEvent()
    data class ActionError(val message: String) : ItemEvent()
}

class ItemDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    var name by mutableStateOf("")
    var unit by mutableStateOf(UnitMeasureType.UNIDADES)
    var unitPrice by mutableStateOf("")
    var stock by mutableStateOf("")
    var dateInputText by mutableStateOf("") // NEW: For raw date input from TextField

    var availableProviders by mutableStateOf<List<ProviderResource>>(emptyList())
    var selectedProvider by mutableStateOf<ProviderResource?>(null)

    var isLoading by mutableStateOf(false)
        private set
    var supplyItem by mutableStateOf<SupplyItemResource?>(null)
        private set

    private val _events = MutableSharedFlow<ItemEvent>()
    val events = _events.asSharedFlow()

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L
    private val itemId: String? = savedStateHandle.get<String>("itemId")

    init {
        loadProviders()
        itemId?.let {
            loadItem(it.toLong())
        }
    }

    private fun loadProviders() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.contactsApi.getProviders(portfolioId)
                if (response.isSuccessful && response.body() != null) {
                    availableProviders = response.body()!!
                    if (itemId != null && supplyItem != null) {
                        selectedProvider = availableProviders.find { it.id == supplyItem?.providerId }
                    }
                } else {
                    Log.e("ItemDetailViewModel", "Error loading providers: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ItemDetailViewModel", "Network error loading providers: ${e.message}")
            }
        }
    }

    private fun loadItem(id: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getSupplyItemById(id)
                if (response.isSuccessful && response.body() != null) {
                    supplyItem = response.body()
                    name = supplyItem?.name ?: ""
                    unit = UnitMeasureType.valueOf(supplyItem?.unit ?: UnitMeasureType.UNIDADES.name)
                    unitPrice = supplyItem?.unitPrice?.toString() ?: ""
                    stock = supplyItem?.stock?.toString() ?: ""

                    // NEW: Update dateInputText from loaded expiredDate (which is now String?)
                    dateInputText = supplyItem?.expiredDate ?: ""

                    selectedProvider = availableProviders.find { it.id == supplyItem?.providerId }
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
        val unitPriceValue = unitPrice.toDoubleOrNull()
        val stockValue = stock.toDoubleOrNull()
        val selectedProviderIdValue = selectedProvider?.id

        if (name.isBlank()) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("El nombre no puede estar vacío.")) }
            return
        }
        if (unitPriceValue == null || unitPriceValue < 0) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("El precio unitario debe ser un número válido.")) }
            return
        }
        if (stockValue == null || stockValue < 0) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("El stock debe ser un número válido.")) }
            return
        }
        if (selectedProviderIdValue == null || selectedProviderIdValue <= 0) {
            viewModelScope.launch { _events.emit(ItemEvent.ActionError("Debes seleccionar un proveedor válido.")) }
            return
        }

        // NEW: Date validation is still here, but we now send `dateInputText` (String?) directly
        val expiredDateStringToSend: String? = if (dateInputText.isNotBlank()) {
            try {
                // Validate format without converting to LocalDateTime/LocalDate
                LocalDate.parse(dateInputText, DateTimeFormatter.ISO_LOCAL_DATE)
                dateInputText
            } catch (e: DateTimeParseException) {
                viewModelScope.launch { _events.emit(ItemEvent.ActionError("Formato de fecha de vencimiento inválido. Usa YYYY-MM-DD.")) }
                return
            }
        } else {
            null
        }

        isLoading = true
        viewModelScope.launch {
            try {
                if (itemId == null) {
                    val request = CreateSupplyItemRequest(
                        providerId = selectedProviderIdValue,
                        branchId = branchId,
                        name = name,
                        unit = unit.name,
                        unitPrice = unitPriceValue,
                        stock = stockValue,
                        expiredDate = expiredDateStringToSend // NEW: Send String? directly
                    )

                    Log.d("ItemViewModel", "Enviando para crear SupplyItem: ${Gson().toJson(request)}")

                    val response = RetrofitClient.productApi.createSupplyItem(request)

                    if (response.isSuccessful) {
                        Log.d("ItemViewModel", "Creación exitosa: ${response.body()}")
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        Log.e("ItemViewModel", "Error al crear: ${response.code()} - $errorBody")
                        _events.emit(ItemEvent.ActionError("Error del servidor: $errorBody"))
                    }
                } else {
                    val request = UpdateSupplyItemRequest(
                        name = name,
                        unitPrice = unitPriceValue,
                        stock = stockValue,
                        expiredDate = expiredDateStringToSend // NEW: Send String? directly
                    )
                    Log.d("ItemViewModel", "Enviando para actualizar SupplyItem (ID: $itemId): ${Gson().toJson(request)}")
                    val response = RetrofitClient.productApi.updateSupplyItem(itemId.toLong(), request)
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
                    val response = RetrofitClient.productApi.deleteSupplyItem(id.toLong())
                    if (response.isSuccessful) {
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error al eliminar."
                        _events.emit(ItemEvent.ActionError("Error del servidor: $errorBody"))
                    }
                } catch (e: Exception) {
                    _events.emit(ItemEvent.ActionError("Error de conexión."))
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // DEFINITION OF THE FACTORY - INTEGRATED
    class ItemDetailViewModelFactory(
        private val portfolioId: String,
        private val selectedSedeId: String,
        private val itemId: Long?
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ItemDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ItemDetailViewModel(
                    savedStateHandle = SavedStateHandle().apply {
                        set("portfolioId", portfolioId)
                        set("selectedSedeId", selectedSedeId)
                        set("itemId", itemId?.toString())
                    }
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}