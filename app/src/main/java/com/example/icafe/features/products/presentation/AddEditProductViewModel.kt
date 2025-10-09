package com.example.icafe.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.features.inventory.data.network.ItemResource
import com.example.icafe.features.inventory.data.network.UnitMeasureType
import com.example.icafe.features.products.data.network.ProductComponent
import com.example.icafe.features.products.data.network.ProductRequest
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.features.products.data.network.ProductStatus
import com.example.icafe.features.products.data.network.ProductType
import com.example.icafe.features.products.data.network.UpdateProductRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddEditProductUiState {
    object Loading : AddEditProductUiState()
    object LoadingItems : AddEditProductUiState()
    data class Success(val message: String) : AddEditProductUiState()
    data class Error(val message: String) : AddEditProductUiState()
    data class Editing(val product: ProductResource) : AddEditProductUiState()
}

data class ProductFormState(
    val name: String = "",
    val category: String = "",
    val portions: Int = 1,
    val steps: String = "",
    val selectedComponents: List<ProductComponentForm> = emptyList(),
    val availableItems: List<ItemResource> = emptyList()
)

data class ProductComponentForm(
    val itemId: Long,
    val itemName: String,
    val unitMeasure: UnitMeasureType,
    val quantity: Double = 0.0
)

class AddEditProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AddEditProductUiState>(AddEditProductUiState.LoadingItems)
    val uiState: StateFlow<AddEditProductUiState> = _uiState

    private val _formState = MutableStateFlow(ProductFormState())
    val formState: StateFlow<ProductFormState> = _formState

    private var productId: Long? = null

    init {
        // Cargar insumos disponibles inmediatamente
        loadAvailableItems()
    }

    fun setProductId(id: Long?) {
        productId = id
        if (id != null) {
            loadProductForEdit(id)
        }
    }

    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name)
    }

    fun updateCategory(category: String) {
        _formState.value = _formState.value.copy(category = category)
    }

    fun updatePortions(portions: Int) {
        _formState.value = _formState.value.copy(portions = portions)
    }

    fun updateSteps(steps: String) {
        _formState.value = _formState.value.copy(steps = steps)
    }

    fun addComponent(item: ItemResource, quantity: Double) {
        val currentComponents = _formState.value.selectedComponents.toMutableList()
        val existingIndex = currentComponents.indexOfFirst { it.itemId == item.id }
        
        if (existingIndex >= 0) {
            // Actualizar cantidad existente
            currentComponents[existingIndex] = currentComponents[existingIndex].copy(quantity = quantity)
        } else {
            // Agregar nuevo componente
            currentComponents.add(
                ProductComponentForm(
                    itemId = item.id,
                    itemName = item.nombre,
                    unitMeasure = item.unidadMedida,
                    quantity = quantity
                )
            )
        }
        
        _formState.value = _formState.value.copy(selectedComponents = currentComponents)
    }

    fun removeComponent(itemId: Long) {
        val currentComponents = _formState.value.selectedComponents.toMutableList()
        currentComponents.removeAll { it.itemId == itemId }
        _formState.value = _formState.value.copy(selectedComponents = currentComponents)
    }

    fun saveProduct() {
        if (!isFormValid()) return

        _uiState.value = AddEditProductUiState.Loading
        viewModelScope.launch {
            try {
                val formData = _formState.value
                val components = formData.selectedComponents.map { 
                    ProductComponent(it.itemId, it.quantity)
                }

                val productRequest = ProductRequest(
                    ownerId = 1L, // TODO: Obtener del usuario autenticado
                    branchId = 1L, // TODO: Obtener de la sede seleccionada
                    name = formData.name,
                    category = formData.category,
                    type = ProductType.SIMPLE,
                    portions = formData.portions,
                    steps = formData.steps,
                    directItem = null, // TODO: Implementar si es necesario
                    components = components
                )

                val updateRequest = UpdateProductRequest(
                    name = formData.name,
                    category = formData.category,
                    type = ProductType.SIMPLE,
                    status = ProductStatus.ACTIVE,
                    portions = formData.portions,
                    steps = formData.steps,
                    directItem = null, // TODO: Implementar si es necesario
                    components = components
                )

                if (productId != null) {
                    // Actualizar producto existente
                    val response = RetrofitClient.productApi.updateProduct(productId!!,
                        updateRequest)
                    if (response.isSuccessful) {
                        _uiState.value = AddEditProductUiState.Success("Producto actualizado correctamente")
                    } else {
                        _uiState.value = AddEditProductUiState.Error("Error al actualizar el producto")
                    }
                } else {
                    // Crear nuevo producto
                    val response = RetrofitClient.productApi.addProduct(productRequest)
                    if (response.isSuccessful) {
                        _uiState.value = AddEditProductUiState.Success("Producto creado correctamente")
                    } else {
                        _uiState.value = AddEditProductUiState.Error("Error al crear el producto")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun loadAvailableItems() {
        _uiState.value = AddEditProductUiState.LoadingItems
        viewModelScope.launch {
            try {
                val response = RetrofitClient.inventoryApi.getItems()
                if (response.isSuccessful && response.body() != null) {
                    _formState.value = _formState.value.copy(availableItems = response.body()!!)
                    _uiState.value = AddEditProductUiState.Success("")
                } else {
                    _uiState.value = AddEditProductUiState.Error("Error al cargar los insumos disponibles")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun loadProductForEdit(productId: Long) {
        _uiState.value = AddEditProductUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.productApi.getProductById(productId)
                if (response.isSuccessful && response.body() != null) {
                    val product = response.body()!!
                    val components = product.components.map { component ->
                        // Buscar el item correspondiente
                        val item = _formState.value.availableItems.find { it.id == component.itemId }
                        ProductComponentForm(
                            itemId = component.itemId,
                            itemName = item?.nombre ?: "Item no encontrado",
                            unitMeasure = item?.unidadMedida ?: UnitMeasureType.GRAMOS,
                            quantity = component.quantity
                        )
                    }
                    
                    _formState.value = ProductFormState(
                        name = product.name,
                        category = product.category,
                        portions = product.portions,
                        steps = product.steps,
                        selectedComponents = components,
                        availableItems = _formState.value.availableItems
                    )
                    
                    _uiState.value = AddEditProductUiState.Editing(product)
                } else {
                    _uiState.value = AddEditProductUiState.Error("Error al cargar el producto")
                }
            } catch (e: Exception) {
                _uiState.value = AddEditProductUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    private fun isFormValid(): Boolean {
        val formData = _formState.value
        return formData.name.isNotBlank() && 
               formData.category.isNotBlank() && 
               formData.portions > 0 &&
               formData.selectedComponents.isNotEmpty()
    }
}
