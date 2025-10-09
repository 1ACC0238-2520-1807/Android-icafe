package com.example.icafe.features.products.data.network

import com.google.gson.annotations.SerializedName

// Enum para el tipo de producto
enum class ProductType {
    SIMPLE,
    COMPOSED
}

// Enum para el estado del producto
enum class ProductStatus {
    ACTIVE,
    INACTIVE
}

// Modelo que representa un componente directo (item único)
data class DirectItem(
    @SerializedName("itemId")
    val itemId: Long,
    @SerializedName("quantity")
    val quantity: Double
)

// Modelo que representa un componente del producto
data class ProductComponent(
    @SerializedName("itemId")
    val itemId: Long,
    @SerializedName("quantity")
    val quantity: Double
)

// Modelo que recibimos del backend para un producto
data class ProductResource(
    @SerializedName("id")
    val id: Long,
    @SerializedName("ownerId")
    val ownerId: Long,
    @SerializedName("branchId")
    val branchId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("type")
    val type: ProductType,
    @SerializedName("status")
    val status: ProductStatus,
    @SerializedName("portions")
    val portions: Int,
    @SerializedName("steps")
    val steps: String,
    @SerializedName("directItem")
    val directItem: DirectItem?,
    @SerializedName("components")
    val components: List<ProductComponent>,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("version")
    val version: Int
)

// Modelo para CREAR un producto
data class ProductRequest(
    @SerializedName("ownerId")
    val ownerId: Long,
    @SerializedName("branchId")
    val branchId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("type")
    val type: ProductType,
    @SerializedName("portions")
    val portions: Int,
    @SerializedName("steps")
    val steps: String,
    @SerializedName("directItem")
    val directItem: DirectItem?,
    @SerializedName("components")
    val components: List<ProductComponent>
)

// Modelo para ACTUALIZAR un producto
data class UpdateProductRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("type")
    val type: ProductType,
    @SerializedName("status")
    val status: ProductStatus,
    @SerializedName("portions")
    val portions: Int,
    @SerializedName("steps")
    val steps: String,
    @SerializedName("directItem")
    val directItem: DirectItem?,
    @SerializedName("components")
    val components: List<ProductComponent>
)
