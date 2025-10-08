package com.example.icafe.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.icafe.features.auth.presentation.login.Login
import com.example.icafe.features.auth.presentation.register.Register
import com.example.icafe.features.contacts.presentation.employee.AddEditEmployeeScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeDetailScreen
import com.example.icafe.features.contacts.presentation.employee.EmployeeListScreen
import com.example.icafe.features.home.presentation.home.HomeScreen
import com.example.icafe.features.contacts.presentation.provider.AddEditProviderScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderDetailScreen
import com.example.icafe.features.contacts.presentation.provider.ProviderListScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Route.Login.route) {
        // --- Flujo de Autenticación ---
        composable(Route.Login.route) {
            Login(
                onSubmit = { userId ->
                    navController.navigate(Route.Home.createRoute(portfolioId = userId.toString())) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                },
                onRegister = {
                    navController.navigate(Route.Register.route)
                }
            )
        }
        composable(Route.Register.route) {
            Register {
                navController.navigate(Route.Login.route) {
                    popUpTo(Route.Login.route) { inclusive = true }
                }
            }
        }

        // --- Pantalla Principal ---
        composable(
            route = Route.Home.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId") ?: "N/A"
            HomeScreen(navController = navController, sedeId = portfolioId)
        }

        // --- Flujo de Empleados ---
        composable(
            route = Route.EmployeeList.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            EmployeeListScreen(navController, portfolioId)
        }

        composable(
            route = Route.AddEmployee.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            AddEditEmployeeScreen(navController, portfolioId, employeeId = null)
        }

        composable(
            route = Route.EmployeeDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val employeeId = backStackEntry.arguments?.getString("employeeId")!!.toLong()
            EmployeeDetailScreen(navController, portfolioId, employeeId)
        }

        composable(
            route = Route.EditEmployee.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val employeeId = backStackEntry.arguments?.getString("employeeId")!!.toLong()
            AddEditEmployeeScreen(navController, portfolioId, employeeId)
        }

        // --- AÑADIR FLUJO DE PROVEEDORES ---
        composable(
            route = Route.ProviderList.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            ProviderListScreen(navController, portfolioId)
        }

        composable(
            route = Route.AddProvider.route,
            arguments = listOf(navArgument("portfolioId") { type = NavType.StringType })
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            AddEditProviderScreen(navController, portfolioId, providerId = null)
        }

        composable(
            route = Route.ProviderDetail.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val providerId = backStackEntry.arguments?.getString("providerId")!!.toLong()
            ProviderDetailScreen(navController, portfolioId, providerId)
        }

        composable(
            route = Route.EditProvider.route,
            arguments = listOf(
                navArgument("portfolioId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val portfolioId = backStackEntry.arguments?.getString("portfolioId")!!
            val providerId = backStackEntry.arguments?.getString("providerId")!!.toLong()
            AddEditProviderScreen(navController, portfolioId, providerId)
        }
    }
}

// --- Definición de todas las rutas de la aplicación ---
sealed class Route(val route: String) {
    object Login : Route("login")
    object Register : Route("register")

    object Home : Route("home/{portfolioId}") {
        fun createRoute(portfolioId: String) = "home/$portfolioId"
    }

    // Rutas para Empleados
    object EmployeeList : Route("employee_list/{portfolioId}") {
        fun createRoute(portfolioId: String) = "employee_list/$portfolioId"
    }
    object AddEmployee : Route("employee_add/{portfolioId}") {
        fun createRoute(portfolioId: String) = "employee_add/$portfolioId"
    }
    object EmployeeDetail : Route("employee_detail/{portfolioId}/{employeeId}") {
        fun createRoute(portfolioId: String, employeeId: Long) = "employee_detail/$portfolioId/$employeeId"
    }
    object EditEmployee : Route("employee_edit/{portfolioId}/{employeeId}") {
        fun createRoute(portfolioId: String, employeeId: Long) = "employee_edit/$portfolioId/$employeeId"
    }


    // --- NUEVAS RUTAS PARA PROVEEDORES ---
    object ProviderList : Route("provider_list/{portfolioId}") {
        fun createRoute(portfolioId: String) = "provider_list/$portfolioId"
    }
    object AddProvider : Route("provider_add/{portfolioId}") {
        fun createRoute(portfolioId: String) = "provider_add/$portfolioId"
    }
    object ProviderDetail : Route("provider_detail/{portfolioId}/{providerId}") {
        fun createRoute(portfolioId: String, providerId: Long) = "provider_detail/$portfolioId/$providerId"
    }
    object EditProvider : Route("provider_edit/{portfolioId}/{providerId}") {
        fun createRoute(portfolioId: String, providerId: Long) = "provider_edit/$portfolioId/$providerId"
    }


    // Rutas para otras secciones
    object Provider : Route("provider")
    object Insumo : Route("insumo")
    object Product : Route("product")
    object Inventory : Route("inventory")
}