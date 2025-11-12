package com.example.icafe.features.finances.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn // Icono para ventas
import androidx.compose.material.icons.filled.ShoppingCart // Icono para compras
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun FinanceLandingScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    AppScaffold(
        title = "Finanzas",
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = OliveGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Gestión Financiera",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Administra tus ventas y compras",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Botón para Ventas
            FinanceLandingButton(
                text = "Administrar Ventas",
                icon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = BrownDark) },
                backgroundColor = Peach,
                onClick = { navController.navigate(Route.SalesList.createRoute(portfolioId, selectedSedeId)) }
            )

            // Botón para Órdenes de Compra
            FinanceLandingButton(
                text = "Administrar Compras",
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BrownDark) },
                backgroundColor = Peach,
                onClick = { navController.navigate(Route.PurchaseOrderList.createRoute(portfolioId, selectedSedeId)) }
            )
        }
    }
}

@Composable
fun FinanceLandingButton(
    text: String,
    icon: @Composable () -> Unit,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = BrownDark,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinanceLandingScreenPreview() {
    ICafeTheme {
        FinanceLandingScreen(rememberNavController(), portfolioId = "1", selectedSedeId = "1")
    }
}