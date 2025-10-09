package com.example.icafe.core.data.network

import com.example.icafe.features.contacts.data.network.ContactsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.icafe.features.inventory.data.network.InventoryApiService
import com.example.icafe.features.products.data.network.ProductApiService

object RetrofitClient {
    private const val BASE_URL = "http://upc-icafebackend-3sger0-aa823d-31-97-13-234.traefik.me/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // --- CAMBIO CLAVE AQUÍ ---
    // Añadimos nuestro AuthInterceptor al cliente HTTP
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor()) // <--- AÑADIR ESTA LÍNEA
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
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

}