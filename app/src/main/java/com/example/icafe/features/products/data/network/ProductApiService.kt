package com.example.icafe.features.products.data.network

import retrofit2.Response
import retrofit2.http.*
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.inventory.data.network.CreateSupplyItemRequest
import com.example.icafe.features.inventory.data.network.UpdateSupplyItemRequest


interface ProductApiService {

    // --- Endpoints de Productos ---

    @GET("/api/v1/products/branch/{branchId}")
    suspend fun getProductsByBranchId(@Path("branchId") branchId: Long): Response<List<ProductResource>>

    @POST("/api/v1/products")
    suspend fun createProduct(@Body productData: CreateProductRequest): Response<ProductResource>

    @GET("/api/v1/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: Long
    ): Response<ProductResource>

    @PUT("/api/v1/products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: Long,
        @Body productData: UpdateProductRequest
    ): Response<ProductResource>

    @DELETE("/api/v1/products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: Long): Response<Unit>

    @POST("/api/v1/products/{productId}/ingredients")
    suspend fun addIngredientToProduct(
        @Path("productId") productId: Long,
        @Body request: AddIngredientRequest
    ): Response<ProductResource>

    @DELETE("/api/v1/products/{productId}/ingredients/{supplyItemId}")
    suspend fun removeIngredientFromProduct(
        @Path("productId") productId: Long,
        @Path("supplyItemId") supplyItemId: Long
    ): Response<Unit>

    @POST("/api/v1/products/{productId}/archive")
    suspend fun archiveProduct(@Path("productId") productId: Long): Response<ProductResource>

    @POST("/api/v1/products/{productId}/activate")
    suspend fun activateProduct(@Path("productId") productId: Long): Response<ProductResource>


    // --- Endpoints de Insumos (Supply Items) ---
    // Backend: /api/v1/supply-items

    @GET("/api/v1/supply-items")
    suspend fun getAllSupplyItems(): Response<List<SupplyItemResource>>

    @GET("/api/v1/supply-items/{branchId}/branch")
    suspend fun getSupplyItemsByBranch(@Path("branchId") branchId: Long): Response<List<SupplyItemResource>>

    // *** RESTAURADO: Espera SupplyItemResource de nuevo ***
    @POST("/api/v1/supply-items")
    suspend fun createSupplyItem(@Body request: CreateSupplyItemRequest): Response<SupplyItemResource>

    @GET("/api/v1/supply-items/{id}")
    suspend fun getSupplyItemById(@Path("id") itemId: Long): Response<SupplyItemResource>

    @PUT("/api/v1/supply-items/{id}")
    suspend fun updateSupplyItem(@Path("id") itemId: Long, @Body request: UpdateSupplyItemRequest): Response<SupplyItemResource>

    @DELETE("/api/v1/supply-items/{id}")
    suspend fun deleteSupplyItem(@Path("id") itemId: Long): Response<Unit>
}