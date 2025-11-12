package com.example.icafe.features.home.presentation.scaffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues // ¡IMPORTANTE: Añadir esta importación!
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.icafe.R
import com.example.icafe.core.Route
import com.example.icafe.core.data.network.TokenManager
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.OliveGreen
import kotlinx.coroutines.launch

data class NavigationItem(
    val label: String,
    val icon: @Composable () -> Unit,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    navController: NavController,
    portfolioId: String?,
    selectedSedeId: String?, // New parameter for selected Sede ID
    content: @Composable (PaddingValues) -> Unit
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isAuthScreen = currentRoute == Route.Login.route || currentRoute == Route.Register.route
    val isSedeScreen = currentRoute?.startsWith(Route.SedeSelection.route.substringBefore('/')) == true ||
            currentRoute?.startsWith(Route.AddEditSede.route.substringBefore('/')) == true

    val navigationItems = listOf(
        NavigationItem(
            label = "Inicio",
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            route = Route.Dashboard.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")
        ),
        NavigationItem(
            label = "Contactos",
            icon = { Icon(Icons.Default.People, contentDescription = "Contactos") },
            route = Route.ContactsLanding.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")
        ),
        NavigationItem(
            label = "Alimentos",
            icon = { Icon(Icons.Default.Fastfood, contentDescription = "Alimentos") },
            route = Route.InventoryLanding.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")
        ),
        NavigationItem(
            label = "Finanzas",
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Finanzas") },
            route = Route.FinanceLanding.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")
        ),
        NavigationItem(
            label = "Movimientos",
            icon = { Icon(Icons.Default.Moving, contentDescription = "Movimientos") },
            route = Route.InventoryMovements.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")
        )
    )

    val drawerItems = listOf(
        NavigationItem(label = "Dashboard", icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }, route = Route.Dashboard.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Empleados", icon = { Icon(Icons.Default.Person, contentDescription = null) }, route = Route.EmployeeList.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Proveedores", icon = { Icon(Icons.Default.LocalShipping, contentDescription = null) }, route = Route.ProviderList.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Insumos", icon = { Icon(Icons.Default.ShoppingBasket, contentDescription = null) }, route = Route.ItemList.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Productos", icon = { Icon(Icons.Default.LocalCafe, contentDescription = null) }, route = Route.ProductList.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Registrar Ventas", icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) }, route = Route.AddSale.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Registrar Compras", icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }, route = Route.AddPurchaseOrder.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Movimientos Inventario", icon = { Icon(Icons.Default.Moving, contentDescription = null) }, route = Route.InventoryMovements.createRoute(portfolioId ?: "0", selectedSedeId ?: "0")),
        NavigationItem(label = "Cerrar Sesión", icon = { Icon(Icons.Default.Logout, contentDescription = null) }, route = Route.Login.route)
    )


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val canNavigateBack = navController.previousBackStackEntry != null && !isAuthScreen && !isSedeScreen

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isAuthScreen && !isSedeScreen,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menú principal",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon = item.icon,
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
                        } else if (!isAuthScreen && !isSedeScreen) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    },
                    actions = {
                        if (canNavigateBack && !isAuthScreen && !isSedeScreen) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (!isAuthScreen && !isSedeScreen) {
                    NavigationBar(containerColor = BrownMedium) {
                        navigationItems.forEach { item ->
                            val selected = currentRoute == item.route.substringBeforeLast('/') ||
                                    currentRoute?.startsWith(item.route.substringBeforeLast('/')) == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                } },
                                icon = item.icon,
                                label = { Text(item.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = OliveGreen,
                                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    selectedTextColor = OliveGreen,
                                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                    indicatorColor = BrownMedium
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues -> // ¡¡¡CAMBIO CLAVE AQUÍ!!! El Scaffold de Material3 te da los paddingValues
            // Superficie que aplica el padding proporcionado por el Scaffold
            Surface(modifier = Modifier.padding(paddingValues)) {
                content(paddingValues) // ¡¡¡CAMBIO CLAVE AQUÍ!!! Pasa los paddingValues a la lambda de contenido
            }
        }
    }
}