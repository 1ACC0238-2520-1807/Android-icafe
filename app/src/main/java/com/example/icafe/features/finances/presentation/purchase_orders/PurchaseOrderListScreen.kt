package com.example.icafe.features.finances.presentation.purchase_orders

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue // Importación para el delegado 'by'
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importación para 'sp'

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.finances.data.network.PurchaseOrderResource
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.lang.Exception


// ViewModel y UiState para PurchaseOrderListScreen (deberían estar en PurchaseOrderListViewModel.kt)
// NO DEBEN ESTAR AQUÍ PARA EVITAR REDECLARACIONES.
// Aquí solo importamos y usamos el ViewModel.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOrderListScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val branchId = selectedSedeId.toLongOrNull() ?: 0L
    val viewModel: PurchaseOrderListViewModel = viewModel(factory = PurchaseOrderListViewModel.Factory(branchId))
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        // Asegurarse de cargar las órdenes de compra cada vez que se entra a la pantalla
        viewModel.loadPurchaseOrders()
    }

    AppScaffold(
        title = "Lista de Compras",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Route.AddPurchaseOrder.createRoute(portfolioId, selectedSedeId)) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Orden de Compra")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OffWhiteBackground)
                    .padding(padding) // Usa el padding del scaffold
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Encabezado de la tabla de órdenes de compra
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrownMedium)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ID Compra",
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Proveedor",
                            modifier = Modifier.weight(1.5f).padding(vertical = 12.dp),
                            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Monto Total",
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido principal (Lista, Carga o Error)
                Box(modifier = Modifier.weight(1f)) {
                    when (uiState) {
                        is PurchaseOrderListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is PurchaseOrderListUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text((uiState as PurchaseOrderListUiState.Error).message, color = MaterialTheme.colorScheme.error) }
                        is PurchaseOrderListUiState.Success -> {
                            val purchaseOrders = (uiState as PurchaseOrderListUiState.Success).purchaseOrders
                            if (purchaseOrders.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No hay órdenes de compra registradas.", textAlign = TextAlign.Center)
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(purchaseOrders, key = { it.id }) { purchaseOrder ->
                                        PurchaseOrderItem(purchaseOrder = purchaseOrder) {
                                            // Navegar al detalle de la orden de compra
                                            navController.navigate(Route.PurchaseOrderDetail.createRoute(portfolioId, selectedSedeId, purchaseOrder.id))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseOrderItem(purchaseOrder: PurchaseOrderResource, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = purchaseOrder.id.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
            Text(
                text = purchaseOrder.providerName,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
            Text(
                text = "S/. ${String.format("%.2f", purchaseOrder.totalAmount)}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
        }
    }
}