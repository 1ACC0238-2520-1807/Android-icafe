package com.example.icafe.features.finances.data.network

import com.google.gson.annotations.SerializedName

// Modelos para Ventas
data class SaleItemRequest(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)

data class CreateSaleRequest(
    val customerId: Long,
    val branchId: Long,
    val items: List<SaleItemRequest>,
    val notes: String? = null
)

// Modelos de recurso (lo que obtenemos del backend)
data class SaleItemResource(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

data class SaleResource(
    val id: Long,
    val customerId: Long,
    val branchId: Long,
    val items: List<SaleItemResource>,
    val totalAmount: Double,
    val saleDate: String, // Mantener como String para que coincida con el backend y el constructor de gson
    val status: String,
    val notes: String?
)