package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.OffWhiteBackground
import com.example.icafe.ui.theme.BrownDark
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.LightGrayBackground
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@Composable
fun PurchaseOrderDetailScreen(navController: NavController, portfolioId: String, selectedSedeId: String, purchaseOrderId: Long) {
    // Instantiate ViewModel
    val viewModel: PurchaseOrderDetailViewModel = viewModel(
        factory = PurchaseOrderDetailViewModel.Factory(portfolioId, selectedSedeId, purchaseOrderId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val branchId = selectedSedeId.toLongOrNull() ?: -1L


    AppScaffold(
        title = "Detalle de Compra",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding ->
        val layoutDirection = LocalLayoutDirection.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(
                    top = 16.dp, // Ajusta el padding superior para iniciar desde arriba
                    start = scaffoldInnerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = scaffoldInnerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    bottom = scaffoldInnerPadding.calculateBottomPadding() // Mantener el padding inferior del Scaffold
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre elementos de la columna principal
        ) {
            when (uiState) {
                is PurchaseOrderDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 64.dp), color = BrownMedium)
                    Text("Cargando detalles de compra...", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                }
                is PurchaseOrderDetailUiState.Error -> {
                    Text(
                        text = (uiState as PurchaseOrderDetailUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 64.dp)
                    )
                    Button(
                        onClick = { viewModel.loadPurchaseOrderDetails(purchaseOrderId, branchId) }, // Lógica para recargar
                        colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
                is PurchaseOrderDetailUiState.Success -> {
                    val purchaseOrder = (uiState as PurchaseOrderDetailUiState.Success).purchaseOrder
                    val resolvedSupplyItemName = (uiState as PurchaseOrderDetailUiState.Success).resolvedSupplyItemName

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Detalle de Orden de Compra",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ID: ${purchaseOrder.id}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Contenido dinámico basado en los datos de la orden de compra
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = LightGrayBackground
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalles adicionales:", style = MaterialTheme.typography.titleMedium, color = BrownDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Proveedor: ${purchaseOrder.providerName}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            // Usa el nombre del insumo resuelto aquí
                            Text("Insumo: $resolvedSupplyItemName", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Cantidad: ${String.format("%.2f", purchaseOrder.quantity)}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Precio Unitario: S/. ${String.format("%.2f", purchaseOrder.unitPrice)}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Monto Total: S/. ${String.format("%.2f", purchaseOrder.totalAmount)}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)

                            val purchaseFormattedDate = try {
                                LocalDateTime.parse(purchaseOrder.purchaseDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            } catch (e: DateTimeParseException) {
                                purchaseOrder.purchaseDate
                            }
                            Text("Fecha de Compra: $purchaseFormattedDate", style = MaterialTheme.typography.bodyMedium, color = BrownDark)

                            val expirationFormattedDate = purchaseOrder.expirationDate?.let {
                                try {
                                    LocalDateTime.parse(it).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                } catch (e: DateTimeParseException) {
                                    it
                                }
                            } ?: "N/A"
                            Text("Fecha de Vencimiento: $expirationFormattedDate", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Estado: ${purchaseOrder.status}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            if (purchaseOrder.notes != null && purchaseOrder.notes.isNotBlank()) {
                                Text("Notas: ${purchaseOrder.notes}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            }
                        }
                    }

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                    ) {
                        Text("Volver a la lista de compras", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}