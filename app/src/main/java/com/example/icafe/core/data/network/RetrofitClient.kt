package com.example.icafe.core.data.network

import com.example.icafe.features.contacts.data.network.ContactsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.icafe.features.inventory.data.network.InventoryApiService
import com.example.icafe.features.products.data.network.ProductApiService
import com.example.icafe.core.data.network.BranchApiService

// Ya no necesitamos importar ninguna clase de java.time aquí
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
// import java.time.LocalDate // REMOVED
// import java.time.LocalDateTime // REMOVED
// import java.time.OffsetDateTime // REMOVED
// import java.time.format.DateTimeFormatter // REMOVED
// import java.time.format.DateTimeParseException // REMOVED

object RetrofitClient {
    private const val BASE_URL = "http://upc-icafebackend-3sger0-aa823d-31-97-13-234.traefik.me/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val gson = GsonBuilder()
        // CAMBIO: Se remueve el deserializador para LocalDate también.
        // Asegúrate de que TODOS tus DTOs que antes usaban LocalDate ahora usen String.
        // .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, typeOfT, context ->
        //     LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
        // })
        .create() // No hay adaptadores de fecha/hora personalizados si todo es String

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(AuthInterceptor())
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
}