package com.example.icafe.core

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
// New import
import com.example.icafe.features.contacts.presentation.ContactsLandingScreen
import com.example.icafe.features.auth.presentation.login.Login
import com.example.icafe.features.auth.presentation.register.Register

// Imports for Contacts (Employees/Providers)
import com.example.icafe.features.contacts.presentation.employee.AddEditEmployeeScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeDetailScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeListScreen
import com.example.icafe.features.contacts.presentation.provider.AddEditProviderScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderDetailScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderListScreen

// Imports for Inventory
import com.example.icafe.features.inventory.presentation.item.AddEditItemScreen
import com.example.icafe.features.inventory.presentation.item.ItemListScreen
import com.example.icafe.features.inventory.presentation.InventoryLandingScreen
import com.example.icafe.features.inventory.presentation.InventoryMovementsScreen

// Imports for Products
import com.example.icafe.features.products.presentation.AddEditProductScreen // Importación CORRECTA
import com.example.icafe.features.products.presentation.ProductListScreen


// Imports for Sede Management
import com.example.icafe.features.sede.presentation.add.AddEditSedeScreen
import com.example.icafe.features.sede.presentation.selection.SedeSelectionScreen

// Imports for Dashboard
import com.example.icafe.features.dashboard.presentation.DashboardScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Route.Login.route) {
        // --- Authentication Flow ---
        composable(Route.Login.route) {
            Login(
                onSubmit = { userId ->
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
            SedeSelectionScreen(navController = navController, portfolioId = portfolioId)
        }
        composable(
            route = Route.AddEditSede.route,
            arguments = listOf(navArgument("sedeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sedeId = backStackEntry.arguments?.getString("sedeId")
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
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0"
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId") ?: "0"
            DashboardScreen(navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId)
        }

        // --- Contacts Landing Screen (NEW COMPOSABLE) ---
        composable(
            route = Route.ContactsLanding.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
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
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0"
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId") ?: "0"
            InventoryLandingScreen(navController, portfolioId, selectedSedeId)
        }
        composable(
            route = Route.ItemList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!! // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            ItemListScreen(navController, portfolioId, selectedSedeId) // ADDED portfolioId
        }
        composable(
            route = Route.AddItem.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!! // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = null) // ADDED portfolioId
        }
        composable(
            route = Route.ItemDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!! // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val itemId = backStackEntry.arguments?.getString("itemId")!!.toLong()
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = itemId) // ADDED portfolioId
        }
        composable(
            route = Route.EditItem.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!! // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val itemId = backStackEntry.arguments?.getString("itemId")!!.toLong()
            AddEditItemScreen(navController, portfolioId, selectedSedeId, itemId = itemId) // ADDED portfolioId
        }
        composable(
            route = Route.InventoryMovements.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED here too for consistency
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0" // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            InventoryMovementsScreen(navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId)
        }

        // --- Products Flow ---
        composable(
            route = Route.ProductList.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED here for consistency
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0" // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            ProductListScreen(navController, portfolioId, selectedSedeId) // ADDED portfolioId
        }
        composable(
            route = Route.AddProduct.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED here for consistency
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0" // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = null) // ADDED portfolioId
        }
        composable(
            route = Route.ProductDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED here for consistency
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0" // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val productId = backStackEntry.arguments?.getString("productId")!!.toLong()
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = productId) // ADDED portfolioId
        }
        composable(
            route = Route.EditProduct.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType }, // ADDED here for consistency
                navArgument("selectedSedeId") { type = NavType.StringType },
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0" // ADDED
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId")!!
            val productId = backStackEntry.arguments?.getString("productId")!!.toLong()
            AddEditProductScreen(navController, portfolioId, selectedSedeId, productId = productId) // ADDED portfolioId
        }

        // --- Placeholder for Sales and Purchases ---
        composable(
            route = Route.Purchases.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("selectedSedeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0"
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId") ?: "0"
            com.example.icafe.features.home.presentation.scaffold.AppScaffold(
                title = "Compras", navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sección de Compras para Portfolio $portfolioId y Sede $selectedSedeId",
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
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "0"
            val selectedSedeId = backStackEntry.arguments?.getString("selectedSedeId") ?: "0"
            com.example.icafe.features.home.presentation.scaffold.AppScaffold(
                title = "Ventas", navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sección de Ventas para Portfolio $portfolioId y Sede $selectedSedeId",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}