package com.example.icafe.features.inventory.presentation

import android.util.Log // IMPORTANTE: Añadir esta importación
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingCart
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
fun InventoryLandingScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    Log.d("InventoryLandingScreen", "Recibido: portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'") // LOG AÑADIDO

    AppScaffold(
        title = "Inventario",
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
                        text = "Gestión de Alimentos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Administra tus insumos y productos",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Buttons for Insumos, Productos, and Movements
            InventoryLandingButton(
                text = "Administrar Insumos",
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BrownDark) },
                backgroundColor = Peach,
                onClick = {
                    Log.d("InventoryLandingScreen", "Navegando a ItemList con portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'") // LOG AÑADIDO
                    navController.navigate(Route.ItemList.createRoute(portfolioId, selectedSedeId))
                }
            )

            InventoryLandingButton(
                text = "Administrar Productos",
                icon = { Icon(Icons.Default.LocalCafe, contentDescription = null, tint = BrownDark) },
                backgroundColor = Peach,
                onClick = {
                    Log.d("InventoryLandingScreen", "Navegando a ProductList con portfolioId='$portfolioId', selectedSedeId='$selectedSedeId'") // LOG AÑADIDO
                    navController.navigate(Route.ProductList.createRoute(portfolioId, selectedSedeId))
                }
            )

        }
    }
}

@Composable
fun InventoryLandingButton(
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
fun InventoryLandingScreenPreview() {
    ICafeTheme {
        InventoryLandingScreen(rememberNavController(), portfolioId = "1", selectedSedeId = "1")
    }
}