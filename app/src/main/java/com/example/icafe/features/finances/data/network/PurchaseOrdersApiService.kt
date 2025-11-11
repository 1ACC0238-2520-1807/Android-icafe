package com.example.icafe.features.finances.data.network

import retrofit2.Response
import retrofit2.http.*

interface PurchaseOrdersApiService {

    @POST("/api/v1/purchase-orders")
    suspend fun createPurchaseOrder(@Body request: CreatePurchaseOrderRequest): Response<PurchaseOrderResource>

    @GET("/api/v1/purchase-orders/branch/{branchId}")
    suspend fun getPurchaseOrdersByBranchId(@Path("branchId") branchId: Long): Response<List<PurchaseOrderResource>>

    @GET("/api/v1/purchase-orders/{purchaseOrderId}/branch/{branchId}")
    suspend fun getPurchaseOrderById(
        @Path("purchaseOrderId") purchaseOrderId: Long,
        @Path("branchId") branchId: Long
    ): Response<PurchaseOrderResource>

    // Puedes añadir más operaciones de órdenes de compra si es necesario (ej. PUT para confirmar/completar/cancelar)
    @PUT("/api/v1/purchase-orders/{purchaseOrderId}/branch/{branchId}/confirm")
    suspend fun confirmPurchaseOrder(
        @Path("purchaseOrderId") purchaseOrderId: Long,
        @Path("branchId") branchId: Long
    ): Response<PurchaseOrderResource>

    @PUT("/api/v1/purchase-orders/{purchaseOrderId}/branch/{branchId}/complete")
    suspend fun completePurchaseOrder(
        @Path("purchaseOrderId") purchaseOrderId: Long,
        @Path("branchId") branchId: Long
    ): Response<PurchaseOrderResource>

    @PUT("/api/v1/purchase-orders/{purchaseOrderId}/branch/{branchId}/cancel")
    suspend fun cancelPurchaseOrder(
        @Path("purchaseOrderId") purchaseOrderId: Long,
        @Path("branchId") branchId: Long
    ): Response<PurchaseOrderResource>
}