package com.example.icafe.features.contacts.data.network

import retrofit2.Response
import retrofit2.http.*

interface ContactsApiService {

    // Obtener todos los empleados de un portafolio
    @GET("/api/v1/contact-portfolios/{portfolioId}/employees")
    suspend fun getEmployees(@Path("portfolioId") portfolioId: String): Response<List<EmployeeResource>>

    // Añadir un nuevo empleado
    @POST("/api/v1/contact-portfolios/{portfolioId}/employees")
    suspend fun addEmployee(
        @Path("portfolioId") portfolioId: String,
        @Body employeeData: EmployeeRequest
    ): Response<EmployeeResource>

    // Obtener un empleado por su ID
    @GET("/api/v1/contact-portfolios/{portfolioId}/employees/{employeeId}")
    suspend fun getEmployeeById(
        @Path("portfolioId") portfolioId: String,
        @Path("employeeId") employeeId: Long
    ): Response<EmployeeResource>

    // Actualizar un empleado
    @PUT("/api/v1/contact-portfolios/{portfolioId}/employees/{employeeId}")
    suspend fun updateEmployee(
        @Path("portfolioId") portfolioId: String,
        @Path("employeeId") employeeId: Long,
        @Body employeeData: EmployeeRequest
    ): Response<EmployeeResource>

    // Eliminar un empleado
    @DELETE("/api/v1/contact-portfolios/{portfolioId}/employees/{employeeId}")
    suspend fun deleteEmployee(
        @Path("portfolioId") portfolioId: String,
        @Path("employeeId") employeeId: Long
    ): Response<Unit>

    @GET("/api/v1/contact-portfolios/{portfolioId}/providers")
    suspend fun getProviders(@Path("portfolioId") portfolioId: String): Response<List<ProviderResource>>

    @POST("/api/v1/contact-portfolios/{portfolioId}/providers")
    suspend fun addProvider(
        @Path("portfolioId") portfolioId: String,
        @Body providerData: ProviderRequest
    ): Response<ProviderResource>

    @GET("/api/v1/contact-portfolios/{portfolioId}/providers/{providerId}")
    suspend fun getProviderById(
        @Path("portfolioId") portfolioId: String,
        @Path("providerId") providerId: Long
    ): Response<ProviderResource>

    @PUT("/api/v1/contact-portfolios/{portfolioId}/providers/{providerId}")
    suspend fun updateProvider(
        @Path("portfolioId") portfolioId: String,
        @Path("providerId") providerId: Long,
        @Body providerData: ProviderRequest
    ): Response<ProviderResource>

    @DELETE("/api/v1/contact-portfolios/{portfolioId}/providers/{providerId}")
    suspend fun deleteProvider(
        @Path("portfolioId") portfolioId: String,
        @Path("providerId") providerId: Long
    ): Response<Unit>

}