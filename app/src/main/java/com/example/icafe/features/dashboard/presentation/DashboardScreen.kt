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
import com.example.icafe.ui.theme.*

@Composable
fun DashboardScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    AppScaffold(
        title = "Dashboard",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoSedeCard(sedeName = "Sede $selectedSedeId") // Display selected sede name

            // Placeholder for charts/metrics
            ChartPlaceholderCard(title = "Productos más vendidos de la semana")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Días de Inventario",
                    value = "30", // Placeholder
                    unit = "días"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Días de Expiración",
                    value = "15", // Placeholder
                    unit = "días"
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Días de Homogeneización",
                    value = "20", // Placeholder
                    unit = "días"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Días de Vencimiento",
                    value = "25", // Placeholder
                    unit = "días"
                )
            }

            // REMOVED ALL ACTION BUTTONS FROM HERE
            // Their functionalities are accessible via the bottom navigation bar and the side drawer.

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
                    imageVector = Icons.Default.Store, // Changed to store icon
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

// DashboardActionCard and DashboardLargeButton are no longer used in DashboardScreen.
// You can remove their definitions if they are not used elsewhere,
// or keep them if you plan to reuse them in other parts of your app.
// For now, I'm keeping them as they are utility components.

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
fun ChartPlaceholderCard(title: String) {
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
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )
            // Placeholder for a bar chart
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(LightGrayBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Gráfico aquí", color = Color.Gray)
            }
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier = Modifier, title: String, value: String, unit: String) {
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
                fontSize = 32.sp,
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