package com.example.icafe.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.icafe.core.AppNav
import com.example.icafe.ui.theme.ICafeTheme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.util.Log

// Nuevas importaciones necesarias para las pantallas de Finanzas
import com.example.icafe.features.contacts.presentation.ContactsLandingScreen
import com.example.icafe.features.auth.presentation.login.Login
import com.example.icafe.features.auth.presentation.register.Register

// Importaciones para Contactos (Empleados/Proveedores)
import com.example.icafe.features.contacts.presentation.employee.AddEditEmployeeScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeDetailScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeListScreen
import com.example.icafe.features.contacts.presentation.provider.AddEditProviderScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderDetailScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderListScreen

// Importaciones para Inventario
import com.example.icafe.features.inventory.presentation.item.AddEditItemScreen
import com.example.icafe.features.inventory.presentation.item.ItemListScreen
import com.example.icafe.features.inventory.presentation.InventoryLandingScreen
import com.example.icafe.features.inventory.presentation.InventoryMovementsScreen

// Importaciones para Productos
import com.example.icafe.features.products.presentation.AddEditProductScreen
import com.example.icafe.features.products.presentation.ProductListScreen

// Importaciones para Gestión de Sedes
import com.example.icafe.features.sede.presentation.add.AddEditSedeScreen
import com.example.icafe.features.sede.presentation.selection.SedeSelectionScreen

// Importaciones para Dashboard
import com.example.icafe.features.dashboard.presentation.DashboardScreen

// Importaciones para Finanzas (NUEVAS)
import com.example.icafe.features.finances.presentation.FinanceLandingScreen
import com.example.icafe.features.finances.presentation.sales.AddSaleScreen
import com.example.icafe.features.finances.presentation.sales.SalesDetailScreen
import com.example.icafe.features.finances.presentation.sales.SalesListScreen
import com.example.icafe.features.finances.presentation.purchase_orders.AddPurchaseOrderScreen
import com.example.icafe.features.finances.presentation.purchase_orders.PurchaseOrderDetailScreen
import com.example.icafe.features.finances.presentation.purchase_orders.PurchaseOrderListScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ICafeTheme {
                AppNav()
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Route.Login.route) {
        // --- Authentication Flow ---
        composable(Route.Login.route) {
            Login(
                onSubmit = { userId ->
                    Log.d("AppNav", "Login exitoso, navegando a SedeSelection con portfolioId: $userId")
                    navController.navigate(Route.SedeSelection.createRoute(portfolioId = userId.toString())) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onRegister = {
                    navController.navigate(Route.Register.route)
                }
            )
        }
        composable(Route.Register.route) {
            Register(
                onSubmit = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Sede Selection and Management ---
        composable(
            route = Route.SedeSelection.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0"
            Log.d("AppNav", "En SedeSelectionScreen: Extrayendo portfolioId='$portfolioId'")
            SedeSelectionScreen(navController = navController, portfolioId = portfolioId)
        }
        composable(
            route = Route.AddEditSede.route,
            arguments = listOf(navArgument("sedeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sedeId = backStackEntry.arguments?.getString("sedeId")
            Log.d("AppNav", "En AddEditSedeScreen: Extrayendo sedeId='$sedeId'")
            AddEditSedeScreen(navController = navController, sedeId = sedeId)
        }

        // --- Dashboard Screen (New Home) ---
        composable(
            route = Route.Dashboard.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En DashboardScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            DashboardScreen(navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId)
        }

        // --- Contacts Landing Screen ---
        composable(
            route = Route.ContactsLanding.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En ContactsLandingScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            ContactsLandingScreen(navController, portfolioId, selectedSedeId)
        }

        // --- Contacts Flow (Employees/Providers) ---
        composable(
            route = Route.EmployeeList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En EmployeeListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            EmployeeListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddEmployee.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddEmployeeScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddEditEmployeeScreen(navController, portfolioId, selectedSedeId, employeeId = null)
        }
        composable(
            route = Route.EmployeeDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val employeeId = backStackEntry.arguments?.getString("employeeId")!!.toLong()
            Log.d("AppNav", "En EmployeeDetailScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', employeeId=$employeeId")
            EmployeeDetailScreen(navController, portfolioId, selectedSedeId, employeeId)
        }
        composable(
            route = Route.EditEmployee.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val employeeId = backStackEntry.arguments?.getString("employeeId")!!.toLong()
            Log.d("AppNav", "En EditEmployeeScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', employeeId=$employeeId")
            AddEditEmployeeScreen(navController, portfolioId, selectedSedeId, employeeId)
        }

        composable(
            route = Route.ProviderList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En ProviderListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            ProviderListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddProvider.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddProviderScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddEditProviderScreen(navController, portfolioId, selectedSedeId, providerId = null)
        }
        composable(
            route = Route.ProviderDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val providerId = backStackEntry.arguments?.getString("providerId")!!.toLong()
            Log.d("AppNav", "En ProviderDetailScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', providerId=$providerId")
            ProviderDetailScreen(navController, portfolioId, selectedSedeId, providerId)
        }
        composable(
            route = Route.EditProvider.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val providerId = backStackEntry.arguments?.getString("providerId")!!.toLong()
            Log.d("AppNav", "En EditProviderScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', providerId=$providerId")
            AddEditProviderScreen(navController, portfolioId, selectedSedeId, providerId)
        }

        // --- Inventory Flow ---
        composable(
            route = Route.InventoryLanding.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En InventoryLandingScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            InventoryLandingScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.ItemList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En ItemListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            ItemListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddItem.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddItemScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = null)
        }
        composable(
            route = Route.ItemDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val itemId = backStackEntry.arguments?.getString("itemId")!!.toLong()
            Log.d("AppNav", "En ItemDetailScreen (Edit): Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', itemId=$itemId")
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = itemId)
        }
        composable(
            route = Route.EditItem.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val itemId = backStackEntry.arguments?.getString("itemId")!!.toLong()
            Log.d("AppNav", "En EditItemScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', itemId=$itemId")
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = itemId)
        }
        composable(
            route = Route.InventoryMovements.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En InventoryMovementsScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            InventoryMovementsScreen(navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId)
        }

        // --- Products Flow ---
        composable(
            route = Route.ProductList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En ProductListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            ProductListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddProduct.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddProductScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = null)
        }
        composable(
            route = Route.ProductDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val productId = backStackEntry.arguments?.getString("productId")!!.toLong()
            Log.d("AppNav", "En ProductDetailScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', productId=$productId")
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = productId)
        }
        composable(
            route = Route.EditProduct.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val productId = backStackEntry.arguments?.getString("productId")!!.toLong()
            Log.d("AppNav", "En EditProductScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', productId=$productId")
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = productId)
        }

        // --- Finance Flow (NUEVO) ---
        composable(
            route = Route.FinanceLanding.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En FinanceLandingScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            FinanceLandingScreen(navController, portfolioId, selectedSedeId)
        }
        // Pantallas de Ventas
        composable(
            route = Route.SalesList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En SalesListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            SalesListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddSale.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddSaleScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddSaleScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.SalesDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("saleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val saleId = backStackEntry.arguments?.getString("saleId")!!.toLong()
            Log.d("AppNav", "En SalesDetailScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', saleId=$saleId")
            SalesDetailScreen(navController, portfolioId, selectedSedeId, saleId)
        }

        // Pantallas de Órdenes de Compra
        composable(
            route = Route.PurchaseOrderList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En PurchaseOrderListScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            PurchaseOrderListScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.AddPurchaseOrder.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En AddPurchaseOrderScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            AddPurchaseOrderScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.PurchaseOrderDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("purchaseOrderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val purchaseOrderId = backStackEntry.arguments?.getString("purchaseOrderId")!!.toLong()
            Log.d("AppNav", "En PurchaseOrderDetailScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId', purchaseOrderId=$purchaseOrderId")
            PurchaseOrderDetailScreen(navController, portfolioId, selectedSedeId, purchaseOrderId)
        }


        // Placeholder para futuras características (estos ahora son reemplazados por FinanceLanding, SalesList, etc.)
        composable(
            route = Route.Purchases.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En PurchasesScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            com.example.icafe.features.home.presentation.scaffold.AppScaffold(
                title = "Compras", navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sección de Compras para Portfolio $portfolioId y Sede $selectedSedeId (Old Placeholder)",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
        composable(
            route = Route.Sales.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            Log.d("AppNav", "En SalesScreen: Extrayendo portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'")
            com.example.icafe.features.home.presentation.scaffold.AppScaffold(
                title = "Ventas", navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sección de Ventas para Portfolio $portfolioId y Sede $selectedSedeId (Old Placeholder)",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}