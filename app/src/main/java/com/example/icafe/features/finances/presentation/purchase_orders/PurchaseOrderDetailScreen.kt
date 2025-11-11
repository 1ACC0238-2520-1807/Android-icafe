package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // Importa TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.OffWhiteBackground
import com.example.icafe.ui.theme.BrownDark
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.LightGrayBackground

// Esta pantalla es para mostrar los detalles de una orden de compra específica.
@Composable
fun PurchaseOrderDetailScreen(navController: NavController, portfolioId: String, selectedSedeId: String, purchaseOrderId: Long) {
    AppScaffold(
        title = "Detalle de Compra",
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
                        text = "Detalle de Orden de Compra",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID: $purchaseOrderId",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aquí irían más detalles de la orden de compra, posiblemente fetched por un ViewModel
            // Por ahora, es un placeholder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = LightGrayBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Detalles adicionales...", style = MaterialTheme.typography.bodyLarge, color = BrownDark)
                    Text("Proveedor: [Nombre del proveedor]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Insumo: [Nombre del insumo]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Cantidad: [Cantidad]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Precio Unitario: [Precio]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Monto Total: [Total]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Fecha de Compra: [Fecha]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Fecha de Vencimiento: [Fecha]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                    Text("Estado: [Estado]", style = MaterialTheme.typography.bodyMedium, color = BrownDark)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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