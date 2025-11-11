package com.example.icafe.features.finances.presentation.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.OffWhiteBackground
import com.example.icafe.ui.theme.BrownDark
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.LightGrayBackground

// Esta pantalla es solo un placeholder, puedes expandirla con los detalles de la venta
@Composable
fun SalesDetailScreen(navController: NavController, portfolioId: String, selectedSedeId: String, saleId: Long) {
    AppScaffold(
        title = "Detalle de Venta",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
                        text = "ID: $saleId",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aquí irían más detalles de la venta, posiblemente fetched por un ViewModel
            // Por ahora, es un placeholder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LightGrayBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles adicionales...", style = MaterialTheme.typography.bodyLarge, color = BrownDark)
                    Text("Cliente: [Nombre del cliente]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Monto Total: [Total]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Fecha de Venta: [Fecha]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Estado: [Estado]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    // ... lista de productos vendidos y sus cantidades
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