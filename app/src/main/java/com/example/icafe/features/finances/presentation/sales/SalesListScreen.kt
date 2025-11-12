package com.example.icafe.features.finances.presentation.sales

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
import com.example.icafe.features.finances.data.network.SaleResource
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.lang.Exception



// ViewModel y UiState para SalesListScreen (deberían estar en SalesListViewModel.kt)
// NO DEBEN ESTAR AQUÍ PARA EVITAR REDECLARACIONES.
// Aquí solo importamos y usamos el ViewModel.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesListScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val branchId = selectedSedeId.toLongOrNull() ?: 0L
    val viewModel: SalesListViewModel = viewModel(factory = SalesListViewModel.Factory(branchId))
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        // Asegurarse de cargar las ventas cada vez que se entra a la pantalla
        viewModel.loadSales()
    }

    AppScaffold(
        title = "Lista de Ventas",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Route.AddSale.createRoute(portfolioId, selectedSedeId)) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Venta")
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

                // Encabezado de la tabla de ventas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BrownMedium)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ID Venta",
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Monto Total",
                            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                            textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Fecha",
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
                        is SalesListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is SalesListUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text((uiState as SalesListUiState.Error).message, color = MaterialTheme.colorScheme.error) }
                        is SalesListUiState.Success -> {
                            val sales = (uiState as SalesListUiState.Success).sales
                            if (sales.isEmpty()) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No hay ventas registradas.", textAlign = TextAlign.Center)
                                }
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(sales, key = { it.id }) { sale ->
                                        SaleItem(sale = sale) {
                                            // Navegar al detalle de la venta
                                            navController.navigate(Route.SalesDetail.createRoute(portfolioId, selectedSedeId, sale.id))
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
fun SaleItem(sale: SaleResource, onClick: () -> Unit) {
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
                text = sale.id.toString(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
            Text(
                text = "S/. ${String.format("%.2f", sale.totalAmount)}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
            // Formatear la cadena de fecha
            val formattedDate = try {
                LocalDateTime.parse(sale.saleDate).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                sale.saleDate // Fallback si el parseo falla
            }
            Text(
                text = formattedDate,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark,
                fontSize = 16.sp
            )
        }
    }
}