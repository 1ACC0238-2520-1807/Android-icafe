package com.example.icafe.features.inventory.presentation.item

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.features.inventory.data.network.CreateSupplyItemRequest
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.inventory.data.network.UnitMeasureType
import com.example.icafe.features.inventory.data.network.UpdateSupplyItemRequest
// NUEVAS IMPORTACIONES:
import com.example.icafe.features.inventory.data.network.CreateInventoryTransactionResource // Importar el DTO correcto
import com.example.icafe.features.inventory.data.network.TransactionType // Importar el Enum correcto

import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime // Necesario para el movimiento
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

sealed class ItemEvent {
    object ActionSuccess : ItemEvent()
    data class ActionError(val message: String) : ItemEvent()
}

class ItemDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    var name by mutableStateOf("")
    var unit by mutableStateOf(UnitMeasureType.UNIDADES)
    var unitPrice by mutableStateOf("")
    var stock by mutableStateOf("")
    var dateInputText by mutableStateOf("")

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
        Log.d("ItemDetailViewModel", "Inicializando ItemDetailViewModel. selectedSedeId recibido: $selectedSedeId, branchId (calculado para creación): $branchId")
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
                    // Si estamos editando y ya tenemos el supplyItem, seleccionamos el proveedor correspondiente
                    if (itemId != null && supplyItem != null) {
                        selectedProvider = availableProviders.find { it.id == supplyItem?.providerId }
                    }
                    Log.d("ItemDetailViewModel", "Proveedores cargados: ${availableProviders.size} disponibles.")
                } else {
                    Log.e("ItemDetailViewModel", "Error cargando proveedores: ${response.code()} - ${response.errorBody()?.string()}")
                    _events.emit(ItemEvent.ActionError("Error cargando proveedores."))
                }
            } catch (e: Exception) {
                Log.e("ItemDetailViewModel", "Error de red al cargar proveedores: ${e.message}")
                _events.emit(ItemEvent.ActionError("Error de conexión al cargar proveedores."))
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

                    dateInputText = supplyItem?.expiredDate ?: ""

                    // Asegurarse de que el proveedor se selecciona después de cargar los disponibles
                    selectedProvider = availableProviders.find { it.id == supplyItem?.providerId }
                    Log.d("ItemDetailViewModel", "Item ${id} cargado: Nombre='${name}', BranchID_Cargado=${supplyItem?.branchId}, ExpiredDate='${dateInputText}'")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("ItemDetailViewModel", "Error al cargar insumo ${id}: ${response.code()} - $errorBody")
                    _events.emit(ItemEvent.ActionError("No se pudo cargar el insumo: $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("ItemDetailViewModel", "Error de red al cargar insumo ${id}: ${e.message}", e)
                _events.emit(ItemEvent.ActionError("Error de conexión al cargar insumo."))
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

        val expiredDateStringToSend: String? = if (dateInputText.isNotBlank()) {
            try {
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
                if (itemId == null) { // Lógica para CREAR un nuevo insumo
                    val createRequest = CreateSupplyItemRequest(
                        providerId = selectedProviderIdValue,
                        branchId = branchId,
                        name = name,
                        unit = unit.name,
                        unitPrice = unitPriceValue,
                        stock = stockValue,
                        expiredDate = expiredDateStringToSend
                    )

                    Log.d("ItemDetailViewModel", "Enviando para crear SupplyItem: ${Gson().toJson(createRequest)}")
                    Log.d("ItemDetailViewModel", "  --> branchId ENVIADO en la petición de creación: ${createRequest.branchId}")

                    val createResponse = RetrofitClient.productApi.createSupplyItem(createRequest)

                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        val newSupplyItem = createResponse.body()!!
                        Log.d("ItemDetailViewModel", "Creación de SupplyItem exitosa: ID ${newSupplyItem.id}")

                        // --- NUEVO: Registrar movimiento de inventario ---
                        // El DTO CreateInventoryTransactionResource no requiere 'movementDate' en el frontend
                        val movementRequest = CreateInventoryTransactionResource(
                            type = TransactionType.ENTRADA, // Usar el Enum correcto
                            quantity = stockValue,
                            origin = "Initial Stock", // O "Purchase Initial"
                            supplyItemId = newSupplyItem.id, // Usar el ID del insumo recién creado
                            branchId = branchId
                        )
                        Log.d("ItemDetailViewModel", "Enviando para crear InventoryMovement: ${Gson().toJson(movementRequest)}")

                        // *** CAMBIO AQUÍ: Usar RetrofitClient.inventoryApi.registerMovement ***
                        val movementResponse = RetrofitClient.inventoryApi.registerMovement(movementRequest)

                        if (movementResponse.isSuccessful) {
                            Log.d("ItemDetailViewModel", "Movimiento de inventario registrado exitosamente para SupplyItem ID: ${newSupplyItem.id}")
                            _events.emit(ItemEvent.ActionSuccess)
                        } else {
                            val errorBodyMovement = movementResponse.errorBody()?.string() ?: "Error desconocido"
                            Log.e("ItemDetailViewModel", "Error al crear movimiento de inventario: ${movementResponse.code()} - $errorBodyMovement")
                            _events.emit(ItemEvent.ActionError("Insumo creado, pero hubo un error al registrar el movimiento de inventario: $errorBodyMovement"))
                        }
                        // --- FIN NUEVO ---

                    } else {
                        val errorBodyCreate = createResponse.errorBody()?.string() ?: "Error desconocido"
                        Log.e("ItemDetailViewModel", "Error al crear SupplyItem: ${createResponse.code()} - $errorBodyCreate")
                        _events.emit(ItemEvent.ActionError("Error del servidor al crear insumo: $errorBodyCreate"))
                    }
                } else { // Lógica para ACTUALIZAR un insumo existente
                    val updateRequest = UpdateSupplyItemRequest(
                        name = name,
                        unitPrice = unitPriceValue,
                        stock = stockValue,
                        expiredDate = expiredDateStringToSend
                    )
                    Log.d("ItemDetailViewModel", "Enviando para actualizar SupplyItem (ID: $itemId): ${Gson().toJson(updateRequest)}")
                    val updateResponse = RetrofitClient.productApi.updateSupplyItem(itemId.toLong(), updateRequest)
                    if (updateResponse.isSuccessful) {
                        Log.d("ItemDetailViewModel", "Actualización exitosa del Item ${itemId}.")
                        // Aquí también podrías añadir lógica para un movimiento de ajuste si el stock ha cambiado
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBodyUpdate = updateResponse.errorBody()?.string() ?: "Error desconocido"
                        Log.e("ItemDetailViewModel", "Error al actualizar insumo: ${updateResponse.code()} - $errorBodyUpdate")
                        _events.emit(ItemEvent.ActionError("Error del servidor al actualizar insumo: $errorBodyUpdate"))
                    }
                }
            } catch (e: Exception) {
                Log.e("ItemDetailViewModel", "Excepción de red al guardar insumo: ${e.message}", e)
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
                        Log.d("ItemDetailViewModel", "Eliminación exitosa del Item ${id}.")
                        // Aquí también se podría registrar un movimiento de "Salida" o "Ajuste Negativo"
                        _events.emit(ItemEvent.ActionSuccess)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error al eliminar."
                        Log.e("ItemDetailViewModel", "Error al eliminar insumo: ${response.code()} - $errorBody")
                        _events.emit(ItemEvent.ActionError("Error del servidor: $errorBody"))
                    }
                } catch (e: Exception) {
                    Log.e("ItemDetailViewModel", "Excepción de red al eliminar insumo: ${e.message}", e)
                    _events.emit(ItemEvent.ActionError("Error de conexión al eliminar insumo."))
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