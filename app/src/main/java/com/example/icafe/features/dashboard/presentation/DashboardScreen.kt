package com.example.icafe.features.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*
import androidx.compose.ui.platform.LocalLayoutDirection // Importar LocalLayoutDirection para calculateStartPadding/calculateEndPadding

@Composable
fun DashboardScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(portfolioId, selectedSedeId)
    )
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Inicio",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding ->
        val layoutDirection = LocalLayoutDirection.current // Obtener la dirección del layout

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .verticalScroll(rememberScrollState())
                // Aplicar el padding del AppScaffold, pero ajustando explícitamente el padding superior
                .padding(
                    top = 16.dp, // Reducir el padding superior a un valor fijo más pequeño
                    start = scaffoldInnerPadding.calculateStartPadding(layoutDirection) + 16.dp, // Añadir padding horizontal
                    end = scaffoldInnerPadding.calculateEndPadding(layoutDirection) + 16.dp,   // Añadir padding horizontal
                    bottom = scaffoldInnerPadding.calculateBottomPadding() // Mantener el padding inferior del Scaffold
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 64.dp), color = OliveGreen)
                    Text("Cargando datos...", modifier = Modifier.align(Alignment.CenterHorizontally), color = Color.Gray)
                }
                is DashboardUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 64.dp)
                    )
                    Button(onClick = { viewModel.loadDashboardData() }, colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)) {
                        Text("Reintentar", color = Color.White)
                    }
                }
                is DashboardUiState.Success -> {
                    InfoSedeCard(sedeName = state.sedeName)

                    SalesStatsCard(
                        totalSalesAmount = state.metrics.totalSalesAmount,
                        totalSalesCount = state.metrics.totalSalesCount,
                        averageSaleAmount = state.metrics.averageSaleAmount
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Empleados",
                            value = state.metrics.totalEmployees.toString(),
                            unit = "personas",
                            icon = rememberVectorPainter(Icons.Default.People)
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Proveedores",
                            value = state.metrics.totalProviders.toString(),
                            unit = "empresas",
                            icon = rememberVectorPainter(Icons.Default.LocalShipping)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Insumos",
                            value = state.metrics.totalSupplyItems.toString(),
                            unit = "tipos",
                            icon = rememberVectorPainter(Icons.Default.ShoppingBasket)
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Productos",
                            value = state.metrics.totalProducts.toString(),
                            unit = "tipos",
                            icon = rememberVectorPainter(Icons.Default.LocalCafe)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Ventas Totales",
                            value = String.format("%.2f", state.metrics.totalSalesAmount),
                            unit = "S/.",
                            icon = rememberVectorPainter(Icons.Default.MonetizationOn)
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Compras Totales",
                            value = String.format("%.2f", state.metrics.totalPurchasesAmount),
                            unit = "S/.",
                            icon = rememberVectorPainter(Icons.Default.ShoppingCart)
                        )
                    }

                }
            }
        }
    }
}

// === NUEVO COMPOSABLE: SalesStatsCard ===
@Composable
fun SalesStatsCard(totalSalesAmount: Double, totalSalesCount: Int, averageSaleAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Peach)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen de Ventas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = BrownDark.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SalesStatItem(title = "Total Vendido", value = String.format("S/. %.2f", totalSalesAmount), color = OliveGreen)
                SalesStatItem(title = "Nº Ventas", value = totalSalesCount.toString(), color = BrownMedium)
                SalesStatItem(title = "Promedio Venta", value = String.format("S/. %.2f", averageSaleAmount), color = BrownDark)
            }
        }
    }
}

@Composable
fun SalesStatItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 14.sp, color = BrownDark)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun InfoSedeCard(sedeName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OliveGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = "Sede Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .background(Peach, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    tint = BrownDark
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Sede Actual:", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.White)
                    Text(sedeName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardActionCard(modifier: Modifier = Modifier, title: String, icon: Painter, onClick: () -> Unit) {
    Card(
        modifier = modifier.aspectRatio(1f),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrownMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon, contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 14.sp)
        }
    }
}

@Composable
fun DashboardLargeButton(
    text: String,
    icon: (@Composable () -> Unit)? = null,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (backgroundColor == Peach) BrownDark else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            icon?.invoke()
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, title: String, value: String, unit: String, icon: Painter? = null) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = OliveGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ICafeTheme {
        DashboardScreen(rememberNavController(), portfolioId = "1", selectedSedeId = "1")
    }
}