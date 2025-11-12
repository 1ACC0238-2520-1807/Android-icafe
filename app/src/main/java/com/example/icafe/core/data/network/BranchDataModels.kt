package com.example.icafe.core.data.network

import com.google.gson.annotations.SerializedName

// Modelo de recurso recibido del backend
data class BranchResource(
    val id: Long,
    val name: String,
    val address: String
)

// Modelo de solicitud para crear una sede
data class CreateBranchRequest(
    val ownerId: Long,
    val name: String,
    val address: String
)

// Modelo de solicitud para actualizar una sede
data class UpdateBranchRequest(
    val name: String,
    val address: String
)