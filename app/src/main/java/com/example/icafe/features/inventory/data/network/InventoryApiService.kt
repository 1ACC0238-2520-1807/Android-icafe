package com.example.icafe.features.inventory.data.network

import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDateTime

interface InventoryApiService {

    // --- Endpoints de Movimientos de Inventario (StockMovement) ---

    // *** RESTAURADO: Espera InventoryTransactionResource de nuevo ***
    @POST("/api/v1/inventory/movements")
    suspend fun registerMovement(@Body transactionData: CreateInventoryTransactionResource): Response<InventoryTransactionResource>

    // --- Endpoint para el stock actual (ProductStock) ---
    @GET("/api/v1/inventory/stock/{branchId}/{supplyItemId}")
    suspend fun getCurrentStock(
        @Path("branchId") branchId: Long,
        @Path("supplyItemId") supplyItemId: Long
    ): Response<CurrentStockResource>
}