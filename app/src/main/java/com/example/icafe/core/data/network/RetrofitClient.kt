package com.example.icafe.core.data.network

import com.example.icafe.features.contacts.data.network.ContactsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.icafe.features.inventory.data.network.InventoryApiService
import com.example.icafe.features.products.data.network.ProductApiService
import com.example.icafe.core.data.network.BranchApiService
import com.example.icafe.features.finances.data.network.SalesApiService
import com.example.icafe.features.finances.data.network.PurchaseOrdersApiService
import com.example.icafe.core.data.network.AuthApiService

import com.example.icafe.core.data.network.LocalDateAdapter // Asumo que tienes un adaptador para LocalDate/LocalDateTime
import com.example.icafe.core.data.network.LocalDateTimeAdapter // Asumo que tienes un adaptador para LocalDate/LocalDateTime

import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://upc-icafebackend-3sger0-aa823d-31-97-13-234.traefik.me/"

    // Nivel de logging cambiado a NONE para eliminar logs de OkHttp
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.NONE // Cambiado de .BODY a .NONE
    }

    private val gson = GsonBuilder()
        .create()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val contactsApi: ContactsApiService by lazy {
        retrofit.create(ContactsApiService::class.java)
    }

    val inventoryApi: InventoryApiService by lazy {
        retrofit.create(InventoryApiService::class.java)
    }

    val productApi: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }

    val branchApi: BranchApiService by lazy {
        retrofit.create(BranchApiService::class.java)
    }

    val salesApi: SalesApiService by lazy {
        retrofit.create(SalesApiService::class.java)
    }

    val purchaseOrdersApi: PurchaseOrdersApiService by lazy {
        retrofit.create(PurchaseOrdersApiService::class.java)
    }

}