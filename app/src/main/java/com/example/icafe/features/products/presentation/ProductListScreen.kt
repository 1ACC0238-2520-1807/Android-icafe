package com.example.icafe.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.ui.theme.*
import androidx.compose.ui.platform.LocalLayoutDirection // Importar LocalLayoutDirection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: ProductListViewModel = viewModel(
        factory = ProductListViewModel.ProductListViewModelFactory(portfolioId, selectedSedeId)
    )
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadProducts()
                }
            }
        })
    }

    AppScaffold(
        title = "Productos",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding -> // Este es el padding del AppScaffold (TopBar y BottomBar)
        val layoutDirection = LocalLayoutDirection.current // Obtener la dirección del layout

        Box( // Usamos un Box para poder posicionar el FAB
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground) // Fondo principal de la pantalla
                // Aplicar padding combinado: horizontal estándar + superior personalizado, y padding inferior del scaffold
                .padding(
                    top = 16.dp, // Reducir el padding superior a un valor fijo más pequeño
                    start = scaffoldInnerPadding.calculateStartPadding(layoutDirection) + 16.dp, // Añadir padding horizontal
                    end = scaffoldInnerPadding.calculateEndPadding(layoutDirection) + 16.dp,   // Añadir padding horizontal
                    bottom = scaffoldInnerPadding.calculateBottomPadding() // Mantener el padding inferior del Scaffold
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(), // La columna llena el Box, que ya tiene el padding ajustado
                horizontalAlignment = Alignment.CenterHorizontally,
                // NO USAMOS verticalArrangement aquí para dejar el LazyColumn gestionar su propio espacio
            ) {
                // Spacer(modifier = Modifier.height(16.dp)) // Espaciador inicial ya no es tan necesario aquí, el top padding del Box lo maneja

                // Encabezado de la lista (contenido fijo)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Text(
                            text = "Producto",
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Card(
                        modifier = Modifier.weight(0.5f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Text(
                            text = "Precio",
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espaciador después del encabezado

                // Área para la lista de productos (LazyColumn) o mensajes de estado
                Box(modifier = Modifier.weight(1f)) { // Este Box tomará el espacio restante
                    when (val state = uiState) {
                        is ProductListUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = OliveGreen)
                            }
                        }
                        is ProductListUiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                    Button(
                                        onClick = { viewModel.refreshProducts() },
                                        colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
                                    ) {
                                        Text("Reintentar", color = Color.White)
                                    }
                                }
                            }
                        }
                        is ProductListUiState.Success -> {
                            if (state.products.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "No hay productos registrados.",
                                            textAlign = TextAlign.Center,
                                            fontSize = 16.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                            text = "Presiona el botón '+' para agregar el primero.",
                                            textAlign = TextAlign.Center,
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize(), // Allow LazyColumn to fill the entire available space
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(state.products, key = { it.id }) { product ->
                                        ProductListItem(
                                            product = product,
                                            onClick = {
                                                navController.navigate(Route.ProductDetail.createRoute(portfolioId, selectedSedeId, product.id))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Action Button movido aquí, posicionado manualmente dentro del Box
            FloatingActionButton(
                onClick = { navController.navigate(Route.AddProduct.createRoute(portfolioId, selectedSedeId)) },
                containerColor = OliveGreen, // Color del FAB
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Alinear a la esquina inferior derecha del Box padre
                    .padding(16.dp) // Añadir padding alrededor del FAB
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto", tint = Color.White)
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: ProductResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = product.name,
                modifier = Modifier.weight(1f), // Nombre del producto toma más ancho
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = "$${String.format("%.2f", product.costPrice)}", // Precio formateado con 2 decimales
                modifier = Modifier.weight(0.5f), // Precio toma menos ancho
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.End // Alinea el precio a la derecha
            )
        }
    }
}