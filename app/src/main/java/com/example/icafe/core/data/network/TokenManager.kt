package com.example.icafe.core.data.network

import android.content.Context
// import android.util.Log // Se puede eliminar si no se usa en ninguna otra parte del archivo

object TokenManager {
    private const val PREFS_NAME = "icafe_prefs"
    private const val TOKEN_KEY = "auth_token"

    private var appContext: Context? = null
    private var tokenInMemory: String? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        tokenInMemory = getSharedPreferences()?.getString(TOKEN_KEY, null)
        // Log.d("TokenManager", "Inicializado. Token en memoria: ${tokenInMemory?.take(10)}...") // Comentado
    }

    fun saveToken(newToken: String) {
        tokenInMemory = newToken
        appContext?.let {
            getSharedPreferences(it)?.edit()?.putString(TOKEN_KEY, newToken)?.apply()
            // Log.d("TokenManager", "Token guardado en SharedPreferences: $newToken") // Comentado
        } // ?: Log.e("TokenManager", "Contexto no inicializado, no se pudo guardar el token en SharedPreferences.") // Comentado
    }

    fun getToken(): String? {
        if (tokenInMemory == null) {
            tokenInMemory = getSharedPreferences()?.getString(TOKEN_KEY, null)
            // Log.d("TokenManager", "Token recuperado de SharedPreferences (por primera vez o después de pérdida): ${tokenInMemory?.take(10)}...") // Comentado
        } // else {
        // Log.d("TokenManager", "Token recuperado de memoria: ${tokenInMemory?.take(10)}...") // Comentado
        // }
        return tokenInMemory
    }

    fun clearToken() {
        tokenInMemory = null
        appContext?.let {
            getSharedPreferences(it)?.edit()?.remove(TOKEN_KEY)?.apply()
            // Log.d("TokenManager", "Token limpiado de SharedPreferences y memoria.") // Comentado
        } // ?: Log.e("TokenManager", "Contexto no inicializado, no se pudo limpiar el token en SharedPreferences.") // Comentado
    }

    private fun getSharedPreferences(context: Context? = appContext) =
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}