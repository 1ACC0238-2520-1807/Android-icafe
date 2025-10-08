package com.example.icafe.features.contacts.data.network

// Modelo que recibimos del backend
data class EmployeeResource(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val salary: String,
    val branchId: Long
)

// Modelo que enviamos para crear o actualizar un empleado
// El backend reutiliza el mismo recurso para crear y actualizar
data class EmployeeRequest(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val role: String,
    val salary: String,
    val branchId: Long
)


// Modelo que recibimos del backend
data class ProviderResource(
    val id: Long,
    val nameCompany: String,
    val email: String,
    val phoneNumber: String,
    val ruc: String
)

// Modelo que enviamos para crear o actualizar un proveedor
data class ProviderRequest(
    val nameCompany: String,
    val email: String,
    val phoneNumber: String,
    val ruc: String
)