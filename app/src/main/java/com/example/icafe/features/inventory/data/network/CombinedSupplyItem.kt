package com.example.icafe.features.inventory.data.network

// Reutilizamos SupplyItemResource directamente y le añadimos el campo de stock que queremos mostrar.
// Esto es si tu SupplyItemResource no tiene un campo de 'currentStock' que se actualice dinámicamente.
// Si item.stock en SupplyItemResource ya es el stock actual, no necesitas esto.
data class SupplyItemWithCurrentStock(
    val id: Long,
    val providerId: Long,
    val branchId: Long,
    val name: String,
    val unit: String,
    val unitPrice: Double,
    val stock: Double, // Este sería el stock actual obtenido de getCurrentStock
    val buyDate: String,
    val expiredDate: String?
)