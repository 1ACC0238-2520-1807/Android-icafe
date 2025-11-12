package com.example.icafe.features.products.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.products.data.network.AddIngredientRequest
import com.example.icafe.features.products.data.network.CreateProductRequest
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.features.products.data.network.UpdateProductRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

sealed class AddEditProductUiState {
    object Loading : AddEditProductUiState()
    object LoadingSupplyItems : AddEditProductUiState()
    object ReadyForInput : AddEditProductUiState()
    data class Success(val message: String) : AddEditProductUiState()
    data class Error(val message: String) : AddEditProductUiState()
    data class Editing(val product: ProductResource) : AddEditProductUiState()
}

data class ProductFormState(
    val branchId: Long = 1L,
    val name: String = "",
    val costPrice: String = "",
    val profitMargin: String = "",
    val selectedIngredients: List<ProductIngredientForm> = emptyList(),
    val availableSupplyItems: List<SupplyItemResource> = emptyList()
)

data class ProductIngredientForm(
    val supplyItemId: Long,
    val supplyItemName: String,
    val unit: String,
    val quantity: String = ""
)

class AddEditProductViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<AddEditProductUiState>(AddEditProductUiState.LoadingSupplyItems)
    val uiState: StateFlow<AddEditProductUiState> = _uiState

    private val _formState = MutableStateFlow(ProductFormState())
    val formState: StateFlow<ProductFormState> = _formState

    private val portfolioId: String = savedStateHandle.get<String>("portfolioId")!!
    private val selectedSedeId: String = savedStateHandle.get<String>("selectedSedeId")!!
    private val branchId: Long = selectedSedeId.toLongOrNull() ?: 1L
    private var productId: Long? = savedStateHandle.get<String>("productId")?.toLongOrNull()

    init {
        _formState.value = _formState.value.copy(branchId = branchId)
        loadAvailableSupplyItems()
    }

    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name)
    }

    fun updateCostPrice(costPrice: String) {
        _formState.value = _formState.value.copy(costPrice = costPrice)
    }

    fun updateProfitMargin(profitMargin: String) {
        _formState.value = _formState.value.copy(profitMargin = profitMargin)
    }

    fun addOrUpdateIngredient(supplyItem: SupplyItemResource, quantity: String) {
        val currentIngredients = _formState.value.selectedIngredients.toMutableList()
        val existingIndex = currentIngredients.indexOfFirst { it.supplyItemId == supplyItem.id }

        if (existingIndex >= 0) {
            currentIngredients[existingIndex] = currentIngredients[existingIndex].copy(quantity = quantity)
        } else {
            currentIngredients.add(
                ProductIngredientForm(
                    supplyItemId = supplyItem.id,
                    supplyItemName = supplyItem.name,
                    unit = supplyItem.unit,
                    quantity = quantity
                )
            )
        }
        _formState.value = _formState.value.copy(selectedIngredients = currentIngredients)
    }

    fun removeIngredientFromForm(supplyItemId: Long) {
        val currentIngredients = _formState.value.selectedIngredients.toMutableList()
        currentIngredients.removeAll { it.supplyItemId == supplyItemId }
        _formState.value = _formState.value.copy(selectedIngredients = currentIngredients)
    }

    fun saveProduct() {
        val costPriceValue = _formState.value.costPrice.toDoubleOrNull()
        val profitMarginValue = _formState.value.profitMargin.toDoubleOrNull()

        if (!isFormValid(costPriceValue, profitMarginValue)) {
            _uiState.value = AddEditProductUiState.Error("Por favor, completa todos los campos requeridos y añade al menos un ingrediente.")
            return
        }

        _uiState.value = AddEditProductUiState.Loading
        viewModelScope.launch {
            try {
                val formData = _formState.value

                if (productId != null) {
                    val updateRequest = UpdateProductRequest(
                        name = formData.name,
                        costPrice = costPriceValue!!,
                        profitMargin = profitMarginValue!!
                    )
                    val response = RetrofitClient.productApi.updateProduct(productId!!, updateRequest)
                    if (response.isSuccessful) {
                        updateProductIngredients(productId!!, formData.selectedIngredients)
                        _uiState.value = AddEditProductUiState.Success("Producto actualizado correctamente")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido al actualizar."
                        _uiState.value = AddEditProductUiState.Error(errorBody)
                    }
                } else {
                    val createRequest = CreateProductRequest(
                        branchId = formData.branchId,
                        name = formData.name,
                        costPrice = costPriceValue!!,
                        profitMargin = profitMarginValue!!
                    )
                    val response = RetrofitClient.productApi.createProduct(createRequest)
                    if (response.isSuccessful && response.body() != null) {
                        val newProductId = response.body()!!.id
                        updateProductIngredients(newProductId, formData.selectedIngredients)
                        _uiState.value = AddEditProductUiState.Success("Producto creado correctamente")
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido al crear."
                        _uiState.value = AddEditProductUiState.Error(errorBody)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private suspend fun updateProductIngredients(productId: Long, newIngredients: List<ProductIngredientForm>) {
        val currentProductResponse = RetrofitClient.productApi.getProductById(productId)
        val currentBackendIngredients = if (currentProductResponse.isSuccessful && currentProductResponse.body() != null) {
            val product = currentProductResponse.body()!!
            if (product.branchId != branchId) {
                Log.w("AddEditProductViewModel", "Product $productId does not belong to branch $branchId. Skipping ingredient update.")
                emptyList()
            } else {
                product.ingredients
            }
        } else {
            emptyList()
        }

        val ingredientsToRemove = currentBackendIngredients.filter { backendIng ->
            newIngredients.none { newIng -> newIng.supplyItemId == backendIng.supplyItemId }
        }
        for (ingredient in ingredientsToRemove) {
            // Asegúrate de usar el supplyItemId correcto para eliminar
            RetrofitClient.productApi.removeIngredientFromProduct(productId, ingredient.supplyItemId)
        }

        for (newIngredient in newIngredients) {
            val quantity = newIngredient.quantity.toDoubleOrNull()
            if (quantity != null && quantity > 0) {
                // Si el ingrediente ya existe en el backend, no lo volvemos a añadir
                // Aquí podrías añadir lógica para actualizar la cantidad si ya existe
                val existingBackendIngredient = currentBackendIngredients.find { it.supplyItemId == newIngredient.supplyItemId }
                if (existingBackendIngredient == null) {
                    RetrofitClient.productApi.addIngredientToProduct(productId, AddIngredientRequest(newIngredient.supplyItemId, quantity))
                } else if (existingBackendIngredient.quantity != quantity) {
                    // Si ya existe y la cantidad cambió, podrías optar por eliminarlo y volverlo a añadir
                    // o tener un endpoint específico para actualizar la cantidad de un ingrediente.
                    // Por simplicidad en este ejemplo, no se implementa una lógica de actualización directa aquí.
                    // Se asume que AddIngredientToProduct podría manejar la actualización si el backend lo permite
                    // o se necesitaría un DELETE + POST para actualizar.
                    RetrofitClient.productApi.removeIngredientFromProduct(productId, existingBackendIngredient.supplyItemId)
                    RetrofitClient.productApi.addIngredientToProduct(productId, AddIngredientRequest(newIngredient.supplyItemId, quantity))
                }
            }
        }
    }


    private fun loadAvailableSupplyItems() {
        _uiState.value = AddEditProductUiState.LoadingSupplyItems
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getAllSupplyItems()
                if (response.isSuccessful && response.body() != null) {
                    val allSupplyItems = response.body()!!
                    val filteredSupplyItems = allSupplyItems.filter { it.branchId == branchId }
                    _formState.value = _formState.value.copy(availableSupplyItems = filteredSupplyItems)
                    if (productId != null) {
                        loadProductForEdit(productId!!)
                    } else {
                        _uiState.value = AddEditProductUiState.ReadyForInput
                    }
                } else {
                    _uiState.value = AddEditProductUiState.Error("Error al cargar los insumos disponibles")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun loadProductForEdit(id: Long) {
        _uiState.value = AddEditProductUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getProductById(
                    productId = id
                )
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    if (product.branchId != branchId) {
                        _uiState.value = AddEditProductUiState.Error("El producto no pertenece a la sede seleccionada.")
                        return@launch
                    }

                    val availableSupplyItems = _formState.value.availableSupplyItems

                    val ingredients = product.ingredients.map { ingredient ->
                        // Intenta usar el nombre y la unidad del ingrediente directamente de la respuesta del producto.
                        // Si son nulos, busca en la lista de insumos disponibles.
                        val supplyItemName = ingredient.name ?: availableSupplyItems.find { it.id == ingredient.supplyItemId }?.name ?: "Nombre de insumo no disponible"
                        val unit = ingredient.unit ?: availableSupplyItems.find { it.id == ingredient.supplyItemId }?.unit ?: "unidad"

                        ProductIngredientForm(
                            supplyItemId = ingredient.supplyItemId,
                            supplyItemName = supplyItemName,
                            unit = unit,
                            quantity = ingredient.quantity.toString()
                        )
                    }

                    _formState.value = _formState.value.copy(
                        name = product.name,
                        costPrice = product.costPrice.toString(),
                        profitMargin = product.profitMargin.toString(),
                        selectedIngredients = ingredients
                    )

                    _uiState.value = AddEditProductUiState.Editing(product)
                } else {
                    _uiState.value = AddEditProductUiState.Error("Error al cargar el producto para edición.")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun isFormValid(costPrice: Double?, profitMargin: Double?): Boolean {
        val formData = _formState.value
        return formData.name.isNotBlank() &&
                costPrice != null && costPrice >= 0 &&
                profitMargin != null && profitMargin >= 0 &&
                formData.selectedIngredients.all { it.quantity.toDoubleOrNull() != null && it.quantity.toDouble() > 0 } &&
                formData.selectedIngredients.isNotEmpty()
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String, productId: Long?): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AddEditProductViewModel::class.java)) {
                        return AddEditProductViewModel(
                            savedStateHandle = SavedStateHandle().apply {
                                set("portfolioId", portfolioId)
                                set("selectedSedeId", selectedSedeId)
                                set("productId", productId?.toString())
                            }
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}