package com.example.icafe.features.finances.presentation.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Para LazyColumn de SaleItemResource
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
import java.time.format.DateTimeParseException // Importar para manejar posibles errores de formato

// Esta pantalla muestra los detalles de una venta específica
@Composable
fun SalesDetailScreen(navController: NavController, portfolioId: String, selectedSedeId: String, saleId: Long) {
    val viewModel: SaleDetailViewModel = viewModel(
        factory = SaleDetailViewModel.Factory(portfolioId, selectedSedeId, saleId)
    )
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Detalle de Venta",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding ->
        val layoutDirection = LocalLayoutDirection.current // Obtener la dirección del layout

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(
                    top = 16.dp, // Ajusta el padding superior
                    start = scaffoldInnerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = scaffoldInnerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    bottom = scaffoldInnerPadding.calculateBottomPadding() // Mantener el padding inferior del Scaffold
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre elementos de la columna principal
        ) {
            when (uiState) {
                is SaleDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 64.dp), color = BrownMedium)
                    Text("Cargando detalles de venta...", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                }
                is SaleDetailUiState.Error -> {
                    Text(
                        text = (uiState as SaleDetailUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 64.dp)
                    )
                    Button(onClick = { viewModel.loadSaleDetails(saleId) }, colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)) {
                        Text("Reintentar", color = Color.White)
                    }
                }
                is SaleDetailUiState.Success -> {
                    val sale = (uiState as SaleDetailUiState.Success).sale

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Detalle de Venta",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ID: ${sale.id}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Detalles adicionales de la venta
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = LightGrayBackground
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Detalles adicionales:", style = MaterialTheme.typography.titleMedium, color = BrownDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cliente ID: ${sale.customerId}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Monto Total: S/. ${String.format("%.2f", sale.totalAmount)}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            val formattedDate = try {
                                LocalDateTime.parse(sale.saleDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            } catch (e: DateTimeParseException) {
                                sale.saleDate // Fallback si el parseo falla
                            }
                            Text("Fecha de Venta: $formattedDate", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            Text("Estado: ${sale.status}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            if (sale.notes != null && sale.notes.isNotBlank()) {
                                Text("Notas: ${sale.notes}", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Productos Vendidos:", style = MaterialTheme.typography.titleMedium, color = BrownDark)
                            if (sale.items.isEmpty()) {
                                Text("No hay productos en esta venta.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp) // Limitar altura para hacer el resto de la pantalla deslizable
                                        .padding(top = 8.dp)
                                ) {
                                    items(sale.items) { item ->
                                        // Aquí podrías querer obtener el nombre real del producto si el SaleItemResource no lo incluye
                                        // Por ahora, solo mostramos ID de producto y detalles de la venta
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Producto ID: ${item.productId}", style = MaterialTheme.typography.bodySmall, color = BrownDark)
                                            Text("Cant: ${item.quantity} x S/. ${String.format("%.2f", item.unitPrice)} = S/. ${String.format("%.2f", item.subtotal)}",
                                                style = MaterialTheme.typography.bodySmall, color = BrownDark)
                                        }
                                        Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                    ) {
                        Text("Volver a la lista de ventas", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}