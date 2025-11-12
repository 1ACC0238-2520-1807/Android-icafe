package com.example.icafe.features.finances.data.network

import retrofit2.Response
import retrofit2.http.*

interface SalesApiService {

    @POST("/api/v1/sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Response<SaleResource>

    @GET("/api/v1/sales/branch/{branchId}")
    suspend fun getSalesByBranchId(@Path("branchId") branchId: Long): Response<List<SaleResource>>

    @GET("/api/v1/sales/{saleId}")
    suspend fun getSaleById(@Path("saleId") saleId: Long): Response<SaleResource>

    // Puedes añadir más operaciones de venta si es necesario (ej. PUT para actualizar, DELETE para cancelar)
    @PUT("/api/v1/sales/{saleId}/complete")
    suspend fun completeSale(@Path("saleId") saleId: Long): Response<SaleResource>

    @PUT("/api/v1/sales/{saleId}/cancel")
    suspend fun cancelSale(@Path("saleId") saleId: Long): Response<SaleResource>

    @GET("/api/v1/sales")
    suspend fun getAllSales(): Response<List<SaleResource>>

}
