package com.example.icafe.features.inventory.data.network

import retrofit2.Response
import retrofit2.http.*

interface InventoryApiService {

    // --- Endpoints de Insumos (Supply Items) ---

    @GET("/api/v1/inventory/items")
    suspend fun getItems(): Response<List<ItemResource>>

    @POST("/api/v1/inventory/items")
    suspend fun addItem(@Body itemData: ItemRequest): Response<ItemResource>

    @GET("/api/v1/inventory/items/{id}")
    suspend fun getItemById(@Path("id") itemId: Long): Response<ItemResource>

    @PUT("/api/v1/inventory/items/{id}")
    suspend fun updateItem(@Path("id") itemId: Long, @Body itemData: UpdateItemRequest): Response<ItemResource>

    @DELETE("/api/v1/inventory/items/{id}")
    suspend fun deleteItem(@Path("id") itemId: Long): Response<Unit>
}