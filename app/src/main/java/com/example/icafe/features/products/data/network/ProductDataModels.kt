package com.example.icafe.features.products.data.network
import com.google.gson.annotations.SerializedName
import com.example.icafe.features.inventory.data.network.UnitMeasureType
// Enum para el estado del producto (usado en ProductResource)
enum class ProductStatus {
    ACTIVE,
    ARCHIVED
}
// === NUEVO: ProductIngredientResource (refleja la estructura ProductIngredient del backend) ===
data class ProductIngredientResource(
    @SerializedName("ingredientId") val supplyItemId: Long, // ID del insumo (mapeado desde ingredientId)
    var name: String? = null, // Nombre del insumo (se llena manualmente)
    var unit: String? = null, // Unidad del SupplyItem (se llena manualmente)
    val quantity: Double // Cantidad requerida para este producto
)
// Modelo que recibimos del backend para un producto
data class ProductResource(
    val id: Long,
    val branchId: Long,
    val name: String,
    val costPrice: Double,
    val salePrice: Double,
    val profitMargin: Double,
    val status: ProductStatus,
    val ingredients: List<ProductIngredientResource> // Lista de ingredientes (usando un nuevo recurso)
)
// Modelo para CREAR un producto (coincide con CreateProductCommand del backend)
data class CreateProductRequest(
    val branchId: Long,
    val name: String,
    val costPrice: Double,
    val profitMargin: Double
)
// Modelo para ACTUALIZAR un producto (coincide con UpdateProductCommand del backend)
data class UpdateProductRequest(
    val name: String,
    val costPrice: Double,
    val profitMargin: Double
)
// Modelo para añadir un ingrediente (coincide con AddIngredientCommand del backend)
data class AddIngredientRequest(
    val supplyItemId: Long,
    val quantity: Double
)
