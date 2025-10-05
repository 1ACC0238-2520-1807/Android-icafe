package com.example.icafe.core

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icafe.features.auth.presentation.login.Login

@Composable
fun AppNav(){
    val navController = rememberNavController()

    NavHost(navController, startDestination = Route.Login.route)
    {

        composable(Route.Login.route){
            Login(){
                navController.navigate(Route.Main.route)
            }
        }
    }
}

sealed class Route(val route: String) {

    object Main : Route("main")
    object Login : Route("login")
}