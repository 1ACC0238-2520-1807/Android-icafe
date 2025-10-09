package com.example.icafe.features.products.data.network

import retrofit2.Response
import retrofit2.http.*

interface ProductApiService {

    // --- Endpoints de Productos ---

    @GET("/api/v1/products")
    suspend fun getProducts(): Response<List<ProductResource>>

    @POST("/api/v1/products")
    suspend fun addProduct(@Body productData: ProductRequest): Response<ProductResource>

    @GET("/api/v1/products/{id}")
    suspend fun getProductById(@Path("id") productId: Long): Response<ProductResource>

    @PUT("/api/v1/products/{id}")
    suspend fun updateProduct(@Path("id") productId: Long, @Body productData: UpdateProductRequest): Response<ProductResource>

    @DELETE("/api/v1/products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Long): Response<Unit>

    // --- Endpoint para verificar disponibilidad de ingredientes ---
    @GET("/api/v1/products/{id}/availability")
    suspend fun checkProductAvailability(@Path("id") productId: Long): Response<Boolean>
}
