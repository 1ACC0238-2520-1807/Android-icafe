package com.example.icafe.features.inventory.presentation.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.inventory.data.network.SupplyItemWithCurrentStock
import com.example.icafe.ui.theme.LightGrayBackground
import com.example.icafe.ui.theme.OffWhiteBackground
import com.example.icafe.ui.theme.OliveGreen
import com.example.icafe.ui.theme.BrownMedium

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import androidx.compose.ui.platform.LocalLayoutDirection


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: ItemListViewModel = viewModel(
        factory = ItemListViewModel.ItemListViewModelFactory(portfolioId, selectedSedeId)
    )
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadItems() // Recargar elementos cuando la pantalla se reanuda
                }
            }
        })
    }


    AppScaffold(
        title = "Insumos",
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
                            text = "Nombre",
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
                            text = "Cantidad",
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

                // Área para la lista de insumos (LazyColumn) o mensajes de estado
                Box(modifier = Modifier.weight(1f)) { // Este Box tomará el espacio restante
                    when (val state = uiState) {
                        is ItemListUiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = OliveGreen)
                            }
                        }
                        is ItemListUiState.Error -> {
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
                                        onClick = { viewModel.loadItems() }, // Reintentar cargar insumos
                                        colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
                                    ) {
                                        Text("Reintentar", color = Color.White)
                                    }
                                }
                            }
                        }
                        is ItemListUiState.Success -> {
                            if (state.items.isEmpty()) {
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
                                            text = "No hay insumos registrados.",
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
                                    modifier = Modifier.fillMaxSize(), // Permitir que LazyColumn llene todo el espacio disponible
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(state.items, key = { it.id }) { item ->
                                        SupplyItemListItem(item = item) {
                                            navController.navigate(Route.ItemDetail.createRoute(portfolioId, selectedSedeId, item.id))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Action Button movido aquí, posicionado manualmente dentro del Box
            FloatingActionButton(
                onClick = { navController.navigate(Route.AddItem.createRoute(portfolioId, selectedSedeId)) },
                containerColor = OliveGreen, // Color del FAB
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Alinear a la esquina inferior derecha del Box padre
                    .padding(16.dp) // Añadir padding alrededor del FAB
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Insumo", tint = Color.White)
            }
        }
    }
}

// Componente para cada elemento de la lista de insumos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyItemListItem(item: SupplyItemWithCurrentStock, onClick: () -> Unit) {
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
                text = item.name,
                modifier = Modifier.weight(1f), // Nombre del insumo toma más ancho
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = "${String.format("%.1f", item.stock)} ${item.unit.lowercase()}", // Cantidad formateada
                modifier = Modifier.weight(0.5f), // Cantidad toma menos ancho
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.End // Alinea la cantidad a la derecha
            )
        }
    }
}