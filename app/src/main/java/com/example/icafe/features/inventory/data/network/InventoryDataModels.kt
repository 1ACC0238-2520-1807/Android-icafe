package com.example.icafe.features.inventory.data.network
import com.google.gson.annotations.SerializedName
// Ya no necesitamos importar java.time.LocalDateTime aquí

// Enum para la unidad de medida, debe coincidir con el backend
enum class UnitMeasureType {
    GRAMOS,
    KILOGRAMOS,
    LITROS,
    MILILITROS,
    UNIDADES
}
// Enum para el tipo de movimiento de inventario
enum class TransactionType {
    ENTRADA,
    SALIDA
}
// Modelo de recurso para un insumo (SupplyItem) recibido del backend
data class SupplyItemResource(
    val id: Long,
    val providerId: Long,
    val branchId: Long,
    val name: String,
    val unit: String,
    val unitPrice: Double,
    val stock: Double,
    val buyDate: String, // CAMBIO: De LocalDateTime a String
    val expiredDate: String?
)
// Modelo de solicitud para crear un insumo (SupplyItem)
data class CreateSupplyItemRequest(
    val providerId: Long,
    val branchId: Long,
    val name: String,
    val unit: String,
    val unitPrice: Double,
    val stock: Double,
    val expiredDate: String?
)
// Modelo de solicitud para actualizar un insumo (SupplyItem)
data class UpdateSupplyItemRequest(
    val name: String,
    val unitPrice: Double,
    val stock: Double,
    val expiredDate: String?
)
// Modelo recibido del backend para una Transacción de Inventario (StockMovement)
data class InventoryTransactionResource(
    val id: Long,
    val supplyItemId: Long,
    val branchId: Long,
    val type: TransactionType,
    val quantity: Double,
    val origin: String,
    val movementDate: String // CAMBIO: De LocalDateTime a String
)
// Modelo para CREAR una Transacción de Inventario
data class CreateInventoryTransactionResource(
    @SerializedName("supplyItemId")
    val supplyItemId: Long,
    @SerializedName("branchId")
    val branchId: Long,
    @SerializedName("type")
    val type: TransactionType,
    @SerializedName("quantity")
    val quantity: Double,
    @SerializedName("origin")
    val origin: String
)
// Recurso para el stock actual
data class CurrentStockResource(
    val branchId: Long,
    val supplyItemId: Long,
    val currentStock: Double
)