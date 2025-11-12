package com.example.icafe.features.inventory.data.network

import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDateTime

interface InventoryApiService {

    // --- Endpoints de Movimientos de Inventario (StockMovement) ---

    // *** RESTAURADO: Espera InventoryTransactionResource de nuevo ***
    @POST("/api/v1/inventory/movements")
    suspend fun registerMovement(@Body request: CreateInventoryTransactionResource): Response<Unit>

    // --- Endpoint para el stock actual (ProductStock) ---
    @GET("/api/v1/inventory/stock/{branchId}/{supplyItemId}")
    suspend fun getCurrentStock(
        @Path("branchId") branchId: Long,
        @Path("supplyItemId") supplyItemId: Long
    ): Response<CurrentStockResource>

    @GET("/api/v1/inventory/movements/{branchId}")
    suspend fun getAllStockMovementsByBranch(@Path("branchId") branchId: Long): Response<List<InventoryTransactionResource>>
}