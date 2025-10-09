package com.example.icafe.features.home.presentation.scaffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.icafe.R
import com.example.icafe.core.Route
import com.example.icafe.core.data.network.TokenManager
import kotlinx.coroutines.launch

data class NavigationItem(
    val label: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    navController: NavController,
    portfolioId: String?,
    content: @Composable () -> Unit
) {
    val navigationItems = listOf(
        NavigationItem(label = "Inicio", route = Route.Home.createRoute(portfolioId ?: "0")),
        NavigationItem(label = "Registro Empleado", route = Route.EmployeeList.createRoute(portfolioId ?: "0")),
        NavigationItem(label = "Registro Proveedores", route = Route.ProviderList.createRoute(portfolioId ?: "0")),
        NavigationItem(label = "Gestion Costos", route = "Costos"),
        // --- LÍNEA CORREGIDA AQUÍ ---
        NavigationItem(label = "Registro Insumos", route = Route.ItemList.route),
        NavigationItem(label = "Registro Productos", route = Route.ProductList.route),
        NavigationItem(label = "Inventario", route = Route.Inventory.route),
        NavigationItem(label = "Cerrar Sesión", route = Route.Login.route)
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val canNavigateBack = navController.previousBackStackEntry != null

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú principal",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                navigationItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (item.route == Route.Login.route) {
                                TokenManager.clearToken()
                                navController.navigate(item.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                navController.navigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logoicafe),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(32.dp)
                            )
                            Text(text = title)
                        }
                    },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    },
                    actions = {
                        if (canNavigateBack) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Surface(modifier = Modifier.padding(padding)) {
                content()
            }
        }
    }
}