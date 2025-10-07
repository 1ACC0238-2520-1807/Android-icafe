package com.example.icafe.core

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icafe.features.auth.presentation.login.Login
import com.example.icafe.features.auth.presentation.register.Register
import com.example.icafe.features.home.presentation.home.Home
import com.example.icafe.features.home.presentation.scaffold.AppScaffold

@Composable
fun AppNav(){
        val navController = rememberNavController()

        NavHost(navController, startDestination = Route.Login.route)
        {

            composable(Route.Login.route) {
                // 3. Pasa la acción de navegación al nuevo parámetro
                Login(
                    onSubmit = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Login.route) { inclusive = true }
                        }
                    },
                    onRegister = {
                        navController.navigate(Route.Register.route)
                    }
                )
            }
            composable(Route.Register.route){
                Register(){
                    navController.navigate(Route.Home.route)
                }
            }
            composable(Route.Home.route) {
                AppScaffold(title = "Inicio",navController=navController) {
                    Home() // Aquí colocas el contenido de tu pantalla Home
                }
            }
        }

}

sealed class Route(val route: String) {

    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home") // "home" en minúsculas es la convención
    object Employee : Route("employee")
    object Provider : Route("provider")
    object Insumo : Route("insumo")
    object Product : Route("product")
    object Inventory : Route("inventory")
}