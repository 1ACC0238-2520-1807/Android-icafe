package com.example.icafe.features.home.presentation.scaffold

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.R
import com.example.icafe.core.Route

import com.example.icafe.ui.theme.ICafeTheme
import kotlinx.coroutines.launch


data class NavigationItem(
    val label: String,
    val route: String
)

val navigationItems= listOf(
    NavigationItem(label="Inicio", route = Route.Home.route),
    NavigationItem(label="Registro Empleado", route = Route.Employee.route),
    NavigationItem(label="Registro proveedores", route = Route.Provider.route),
    NavigationItem(label="Gestion Costos", route = "Costos"),
    NavigationItem(label="Registro Insumos", route = Route.Insumo.route),
    NavigationItem(label="Registro Productos", route = Route.Product.route),
    NavigationItem(label="Inventario", route = Route.Inventory.route),
    NavigationItem(label="Cerrar Sesion", route = Route.Login.route)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(title: String,
                navController: NavController,
                content: @Composable () -> Unit) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                        label = {Text(item.label)},
                        selected = false,
                        onClick = {
                            // 2. Usa el NavController para navegar
                            scope.launch {
                                drawerState.close() // Cierra el menú
                            }
                            // Si es "Cerrar Sesion", limpia la pila de navegación
                            if (item.route == Route.Login.route) {
                                navController.navigate(item.route) {
                                    popUpTo(Route.Home.route) { inclusive = true }
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
                        Row (verticalAlignment = Alignment.CenterVertically){
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
                    navigationIcon = {},
                    actions = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            // Contenido principal
            Surface(
                modifier = Modifier.padding(padding)
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppScaffoldPreview() {
    val navController= rememberNavController()
    ICafeTheme {
        AppScaffold(
            title = "Inicio",
            navController = navController
        ){

        }
    }

}