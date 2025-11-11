package com.example.icafe.features.finances.data.network

import com.google.gson.annotations.SerializedName

// Modelos para Órdenes de Compra
data class CreatePurchaseOrderRequest(
    val branchId: Long,
    val providerId: Long,
    val supplyItemId: Long,
    val quantity: Double,
    val unitPrice: Double,
    val purchaseDate: String, // Mantener como String para que coincida con el backend y el constructor de gson
    val expirationDate: String? = null, // Opcional
    val notes: String? = null
)

// Modelo de recurso (lo que obtenemos del backend)
data class PurchaseOrderResource(
    val id: Long,
    val branchId: Long,
    val providerId: Long,
    val providerName: String,
    val providerPhone: String,
    val providerEmail: String,
    val supplyItemId: Long,
    val supplyItemName: String,
    val unitPrice: Double,
    val quantity: Double,
    val totalAmount: Double,
    val purchaseDate: String, // Mantener como String
    val expirationDate: String?,
    val status: String,
    val notes: String?
)