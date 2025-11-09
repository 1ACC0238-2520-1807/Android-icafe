package com.example.icafe.core.data.network

import retrofit2.Response
import retrofit2.http.*

interface BranchApiService {

    @POST("/api/v1/branch")
    suspend fun createBranch(@Body request: CreateBranchRequest): Response<BranchResource>

    @GET("/api/v1/branch/{branchId}")
    suspend fun getBranchById(@Path("branchId") branchId: String): Response<BranchResource>

    @GET("/api/v1/branch/owner/{ownerId}")
    suspend fun getBranchesByOwnerId(@Path("ownerId") ownerId: String): Response<List<BranchResource>>

    @PUT("/api/v1/branch/{branchId}")
    suspend fun updateBranch(
        @Path("branchId") branchId: String,
        @Body request: UpdateBranchRequest
    ): Response<BranchResource>

    @DELETE("/api/v1/branch/{branchId}")
    suspend fun deleteBranch(@Path("branchId") branchId: String): Response<Unit>
}