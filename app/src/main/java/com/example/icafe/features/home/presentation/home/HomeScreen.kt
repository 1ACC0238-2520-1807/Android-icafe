package com.example.icafe.features.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.* // Importar todos los colores del tema

@Composable
fun HomeScreen(navController: NavController, sedeId: String) {
    AppScaffold(
        title = "iCafe",
        navController = navController,
        portfolioId = sedeId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground) // CAMBIO: Fondo general de la pantalla
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoSedeCard(sedeName = "Portfolio #$sedeId")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Registro Empleados",
                    icon = rememberVectorPainter(image = Icons.Default.Person),
                    onClick = { navController.navigate(Route.EmployeeList.createRoute(sedeId)) }
                )
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Registro Proveedores",
                    icon = rememberVectorPainter(image = Icons.Default.Store),
                    onClick = { navController.navigate(Route.ProviderList.createRoute(sedeId)) }
                )
            }

            DashboardLargeButton(
                text = "INVENTARIO",
                icon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = BrownDark) },
                backgroundColor = Peach, // CAMBIO: Color durazno claro
                onClick = { /* Navegar a inventario */ }
            )

            DashboardLargeButton(
                text = "Gestion de Costos",
                backgroundColor = BrownMedium,
                onClick = { /* Navegar a gestion de costos */ }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Registro Insumos",
                    icon = rememberVectorPainter(image = Icons.Default.ShoppingBasket),
                    onClick = { navController.navigate(Route.ItemList.route) }
                )
                DashboardActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Registro Productos",
                    icon = rememberVectorPainter(image = Icons.Default.LocalCafe),
                    onClick = { /* Navegar a registro productos */ }
                )
            }

            DashboardLargeButton(
                text = "REGISTRAR VENTAS",
                backgroundColor = BrownMedium,
                onClick = { /* Navegar a ventas */ }
            )
            DashboardLargeButton(
                text = "REGISTRAR COMPRAS",
                backgroundColor = BrownMedium,
                onClick = { /* Navegar a compras */ }
            )
        }
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
                    imageVector = Icons.Default.Coffee,
                    contentDescription = "Cafeteria Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .background(Peach, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    tint = BrownDark // CAMBIO: Color del ícono
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Mi Cafetería", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(sedeName, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
            IconButton(onClick = { /* Lógica para editar */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ICafeTheme {
        HomeScreen(rememberNavController(), sedeId = "Preview")
    }
}