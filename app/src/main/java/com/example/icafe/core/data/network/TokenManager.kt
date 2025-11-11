package com.example.icafe.core.data.network

import android.content.Context
import android.util.Log

// Necesitarás una forma de inicializar el contexto de la aplicación.
// Un enfoque común es pasarlo una vez al iniciar la app.
object TokenManager {
    private const val PREFS_NAME = "icafe_prefs"
    private const val TOKEN_KEY = "auth_token"

    private var appContext: Context? = null // Se necesita para SharedPreferences
    private var tokenInMemory: String? = null // Cache in memory

    fun initialize(context: Context) {
        appContext = context.applicationContext
        // Carga el token al iniciar si ya estaba guardado
        tokenInMemory = getSharedPreferences()?.getString(TOKEN_KEY, null)
        Log.d("TokenManager", "Inicializado. Token en memoria: ${tokenInMemory?.take(10)}...")
    }

    fun saveToken(newToken: String) {
        tokenInMemory = newToken
        appContext?.let {
            getSharedPreferences(it)?.edit()?.putString(TOKEN_KEY, newToken)?.apply()
            Log.d("TokenManager", "Token guardado en SharedPreferences: $newToken")
        } ?: Log.e("TokenManager", "Contexto no inicializado, no se pudo guardar el token en SharedPreferences.")
    }

    fun getToken(): String? {
        // Siempre intenta recuperar de la memoria primero, luego de SharedPreferences si es null
        if (tokenInMemory == null) {
            tokenInMemory = getSharedPreferences()?.getString(TOKEN_KEY, null)
            Log.d("TokenManager", "Token recuperado de SharedPreferences (por primera vez o después de pérdida): $tokenInMemory")
        } else {
            Log.d("TokenManager", "Token recuperado de memoria: $tokenInMemory")
        }
        return tokenInMemory
    }

    fun clearToken() {
        tokenInMemory = null
        appContext?.let {
            getSharedPreferences(it)?.edit()?.remove(TOKEN_KEY)?.apply()
            Log.d("TokenManager", "Token limpiado de SharedPreferences y memoria.")
        } ?: Log.e("TokenManager", "Contexto no inicializado, no se pudo limpiar el token en SharedPreferences.")
    }

    private fun getSharedPreferences(context: Context? = appContext) =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}