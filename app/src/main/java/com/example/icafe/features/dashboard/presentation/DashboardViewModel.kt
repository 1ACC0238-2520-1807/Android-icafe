package com.example.icafe.features.dashboard.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.icafe.core.data.network.RetrofitClient
import com.example.icafe.core.data.network.BranchApiService
import com.example.icafe.features.contacts.data.network.ContactsApiService
import com.example.icafe.features.products.data.network.ProductApiService
import com.example.icafe.features.finances.data.network.SalesApiService
import com.example.icafe.features.finances.data.network.PurchaseOrdersApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val portfolioId: String,
    private val selectedSedeId: String,
    private val branchApiService: BranchApiService,
    private val contactsApiService: ContactsApiService,
    private val productApiService: ProductApiService,
    private val salesApiService: SalesApiService,
    private val purchaseOrdersApiService: PurchaseOrdersApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // 1. Obtener nombre de la Sede
                var sedeName = "Cargando Sede..."
                if (selectedSedeId.isNotBlank() && selectedSedeId != "0") {
                    val branchResponse = branchApiService.getBranchById(selectedSedeId)
                    if (branchResponse.isSuccessful && branchResponse.body() != null) {
                        sedeName = branchResponse.body()!!.name
                    } else {
                        Log.e("DashboardViewModel", "Error fetching branch name: ${branchResponse.code()} - ${branchResponse.errorBody()?.string()}")
                        sedeName = "Error Sede"
                    }
                } else {
                    sedeName = "Sede no seleccionada"
                }

                val branchIdLong = selectedSedeId.toLongOrNull() ?: 0L
                if (branchIdLong == 0L) {
                    _uiState.value = DashboardUiState.Error("ID de sede inválido.")
                    return@launch
                }

                // 2. Obtener otras métricas en paralelo
                val employeesResponse = contactsApiService.getEmployees(portfolioId)
                val providersResponse = contactsApiService.getProviders(portfolioId)
                val supplyItemsResponse = productApiService.getSupplyItemsByBranch(branchIdLong)
                val productsResponse = productApiService.getProductsByBranchId(branchIdLong)
                val salesResponse = salesApiService.getSalesByBranchId(branchIdLong)
                val purchaseOrdersResponse = purchaseOrdersApiService.getPurchaseOrdersByBranchId(branchIdLong)

                // 3. Procesar respuestas
                val totalEmployees = employeesResponse.body()?.size ?: 0
                val totalProviders = providersResponse.body()?.size ?: 0
                val totalSupplyItems = supplyItemsResponse.body()?.size ?: 0
                val totalProducts = productsResponse.body()?.size ?: 0

                val salesList = salesResponse.body()
                val totalSalesAmount = salesList?.sumOf { it.totalAmount } ?: 0.0
                val totalSalesCount = salesList?.size ?: 0
                val averageSaleAmount = if (totalSalesCount > 0) totalSalesAmount / totalSalesCount else 0.0

                val totalPurchasesAmount = purchaseOrdersResponse.body()?.sumOf { it.totalAmount } ?: 0.0

                val metrics = DashboardMetrics(
                    totalEmployees = totalEmployees,
                    totalProviders = totalProviders,
                    totalSupplyItems = totalSupplyItems,
                    totalProducts = totalProducts,
                    totalSalesAmount = totalSalesAmount,
                    totalSalesCount = totalSalesCount,
                    averageSaleAmount = averageSaleAmount,
                    totalPurchasesAmount = totalPurchasesAmount,
                    daysInventory = "30", // Placeholders
                    daysExpiration = "15", // Placeholders
                    daysHomogenization = "20", // Placeholders
                    daysDue = "25" // Placeholders
                )

                _uiState.value = DashboardUiState.Success(sedeName, metrics)

            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Error de conexión o al cargar datos: ${e.message}")
                Log.e("DashboardViewModel", "Network error loading dashboard data: ${e.message}", e)
            }
        }
    }

    companion object {
        fun Factory(portfolioId: String, selectedSedeId: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                        return DashboardViewModel(
                            portfolioId = portfolioId,
                            selectedSedeId = selectedSedeId,
                            branchApiService = RetrofitClient.branchApi,
                            contactsApiService = RetrofitClient.contactsApi,
                            productApiService = RetrofitClient.productApi,
                            salesApiService = RetrofitClient.salesApi,
                            purchaseOrdersApiService = RetrofitClient.purchaseOrdersApi
                        ) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}