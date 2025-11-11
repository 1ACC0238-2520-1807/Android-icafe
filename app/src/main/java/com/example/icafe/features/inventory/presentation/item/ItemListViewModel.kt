package com.example.icafe.features.inventory.presentation.item

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.inventory.data.network.SupplyItemWithCurrentStock
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

// *** ESTAS DEFINICIONES DEBEN ESTAR SOLO AQUÍ ***
sealed class ItemListUiState {
    object Loading : ItemListUiState()
    data class Success(val items: List<SupplyItemWithCurrentStock>) : ItemListUiState()
    data class Error(val message: String) : ItemListUiState()
}

class ItemListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<ItemListUiState>(ItemListUiState.Loading)
    val uiState: StateFlow<ItemListUiState> = _uiState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L

    init {
        // loadItems() // Comentado o eliminado como se discutió anteriormente
    }

    fun loadItems() {
        Log.d("ItemListViewModel", "loadItems() llamado. Intentando cargar insumos para branchId: $branchId")
        _uiState.value = ItemListUiState.Loading
        viewModelScope.launch {
            try {
                val supplyItemsResponse = RetrofitClient.productApi.getSupplyItemsByBranch(branchId)

                if (supplyItemsResponse.isSuccessful && supplyItemsResponse.body() != null) {
                    val baseSupplyItems = supplyItemsResponse.body()!!
                    Log.d("ItemListViewModel", "API (1ra consulta) regresó ${baseSupplyItems.size} insumos para branchId $branchId.")

                    if (baseSupplyItems.isEmpty()) {
                        Log.d("ItemListViewModel", "  - No se recibieron insumos base de la API para branchId $branchId. La lista estará vacía.")
                        _uiState.value = ItemListUiState.Success(emptyList())
                        return@launch
                    }

                    val combinedItems = baseSupplyItems.map { supplyItem ->
                        async {
                            try {
                                val currentStockResponse = RetrofitClient.inventoryApi.getCurrentStock(branchId, supplyItem.id)
                                if (currentStockResponse.isSuccessful && currentStockResponse.body() != null) {
                                    val currentStock = currentStockResponse.body()!!.currentStock
                                    Log.d("ItemListViewModel", "  - Stock actual para ${supplyItem.name} (ID: ${supplyItem.id}): $currentStock")
                                    SupplyItemWithCurrentStock(
                                        id = supplyItem.id,
                                        providerId = supplyItem.providerId,
                                        branchId = supplyItem.branchId,
                                        name = supplyItem.name,
                                        unit = supplyItem.unit,
                                        unitPrice = supplyItem.unitPrice,
                                        stock = currentStock,
                                        buyDate = supplyItem.buyDate,
                                        expiredDate = supplyItem.expiredDate
                                    )
                                } else {
                                    val errorBodyStock = currentStockResponse.errorBody()?.string() ?: "Error desconocido"
                                    Log.e("ItemListViewModel", "Error al obtener stock para ${supplyItem.name} (ID: ${supplyItem.id}): ${currentStockResponse.code()} - $errorBodyStock")
                                    SupplyItemWithCurrentStock(
                                        id = supplyItem.id, providerId = supplyItem.providerId, branchId = supplyItem.branchId,
                                        name = supplyItem.name, unit = supplyItem.unit, unitPrice = supplyItem.unitPrice,
                                        stock = 0.0, buyDate = supplyItem.buyDate, expiredDate = supplyItem.expiredDate
                                    )
                                }
                            } catch (e: HttpException) {
                                val errorBody = e.response()?.errorBody()?.string() ?: "Error desconocido"
                                Log.e("ItemListViewModel", "HTTP Exception al obtener stock para ${supplyItem.name} (ID: ${supplyItem.id}): Code=${e.code()} - $errorBody", e)
                                SupplyItemWithCurrentStock(
                                    id = supplyItem.id, providerId = supplyItem.providerId, branchId = supplyItem.branchId,
                                    name = supplyItem.name, unit = supplyItem.unit, unitPrice = supplyItem.unitPrice,
                                    stock = 0.0, buyDate = supplyItem.buyDate, expiredDate = supplyItem.expiredDate
                                )
                            } catch (e: Exception) {
                                Log.e("ItemListViewModel", "Excepción de red al obtener stock para ${supplyItem.name} (ID: ${supplyItem.id}): ${e.message}", e)
                                SupplyItemWithCurrentStock(
                                    id = supplyItem.id, providerId = supplyItem.providerId, branchId = supplyItem.branchId,
                                    name = supplyItem.name, unit = supplyItem.unit, unitPrice = supplyItem.unitPrice,
                                    stock = 0.0, buyDate = supplyItem.buyDate, expiredDate = supplyItem.expiredDate
                                )
                            }
                        }
                    }.awaitAll()

                    Log.d("ItemListViewModel", "Mostrando ${combinedItems.size} insumos en la lista (con stock actual).")
                    _uiState.value = ItemListUiState.Success(combinedItems)

                } else {
                    val errorBody = supplyItemsResponse.errorBody()?.string() ?: "Error desconocido al cargar insumos."
                    Log.e("ItemListViewModel", "Error en 1ra consulta (supplyItems): ${supplyItemsResponse.code()} - $errorBody")
                    _uiState.value = ItemListUiState.Error(errorBody)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Error desconocido en el cuerpo de la respuesta"
                Log.e("ItemListViewModel", "HTTP Exception general al cargar insumos: Code=${e.code()}, Mensaje=${e.message()}, Body=${errorBody}", e)
                _uiState.value = ItemListUiState.Error("Error del servidor (${e.code()}): ${errorBody}")
            } catch (e: Exception) {
                Log.e("ItemListViewModel", "Excepción de red general al cargar insumos: ${e.message}", e)
                _uiState.value = ItemListUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    companion object {
        fun ItemListViewModelFactory(portfolioId: String, selectedSedeId: String): androidx.lifecycle.ViewModelProvider.Factory {
            val factoryPortfolioId = portfolioId
            val factorySelectedSedeId = selectedSedeId

            return object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
                        return ItemListViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("portfolioId", factoryPortfolioId)
                                set("selectedSedeId", factorySelectedSedeId)
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}