package com.example.icafe.features.inventory.data.network

import com.google.gson.annotations.SerializedName

// Enum para la unidad de medida, debe coincidir con el backend
enum class UnitMeasureType {
    GRAMOS,
    KILOGRAMOS,
    LITROS,
    MILILITROS,
    UNIDADES
}

// Modelo que recibimos del backend
data class ItemResource(
    val id: Long,
    val nombre: String,
    val unidadMedida: UnitMeasureType,
    val cantidadActual: Double,
    val puntoDeReorden: Double,
    val requiereReabastecimiento: Boolean,
    val supplyManagementId: Long
)

// Modelo para CREAR un insumo
data class ItemRequest(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("unidadMedida")
    val unidadMedida: UnitMeasureType,
    @SerializedName("cantidadInicial")
    val cantidadInicial: Double,
    @SerializedName("puntoDeReorden")
    val puntoDeReorden: Double,
    @SerializedName("supplyManagementId")
    val supplyManagementId: Long
)

// Modelo para ACTUALIZAR un insumo
data class UpdateItemRequest(
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("unidadMedida")
    val unidadMedida: UnitMeasureType,
    @SerializedName("puntoDeReorden")
    val puntoDeReorden: Double
)