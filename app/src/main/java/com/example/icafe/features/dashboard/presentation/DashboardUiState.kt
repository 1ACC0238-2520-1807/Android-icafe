package com.example.icafe.features.dashboard.presentation

// Clase de datos para contener todas las métricas del dashboard
data class DashboardMetrics(
    val totalEmployees: Int = 0,
    val totalProviders: Int = 0,
    val totalSupplyItems: Int = 0,
    val totalProducts: Int = 0,
    val totalSalesAmount: Double = 0.0,
    val totalSalesCount: Int = 0, // NUEVO: Número total de transacciones de ventas
    val averageSaleAmount: Double = 0.0, // NUEVO: Monto promedio por venta
    val totalPurchasesAmount: Double = 0.0,
    // Puedes dejar estos como placeholders si la lógica de cálculo es compleja o aún no está en el backend
    val daysInventory: String = "N/A",
    val daysExpiration: String = "N/A",
    val daysHomogenization: String = "N/A",
    val daysDue: String = "N/A"
)

// Estados posibles de la UI para el Dashboard
sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val sedeName: String,
        val metrics: DashboardMetrics
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}