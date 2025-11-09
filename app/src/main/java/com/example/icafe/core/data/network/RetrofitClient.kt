package com.example.icafe.core.data.network

import com.example.icafe.features.contacts.data.network.ContactsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.icafe.features.inventory.data.network.InventoryApiService
import com.example.icafe.features.products.data.network.ProductApiService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RetrofitClient {
    // Asegúrate de que esta URL sea la de tu backend desplegado.
    // private const val BASE_URL = "http://127.0.0.1:8080"
    private const val BASE_URL = "http://upc-icafebackend-3sger0-aa823d-31-97-13-234.traefik.me/" // Tu URL de producción

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Gson personalizado para LocalDateTime (mantener esto, es bueno para las fechas en otros módulos)
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        })
        .create()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor()) // <--- AQUÍ SE AÑADE EL INTERCEPTOR DE AUTENTICACIÓN
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)) // Usar gson personalizado
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

    // Añadir este nuevo servicio de API para Sedes
    val branchApi: BranchApiService by lazy {
        retrofit.create(BranchApiService::class.java)
    }

}