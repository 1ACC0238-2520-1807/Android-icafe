package com.example.icafe.features.inventory.presentation

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.InventoryTransactionResource
import com.example.icafe.ui.theme.*
import java.time.format.DateTimeFormatter

@Composable
fun InventoryMovementsScreen(navController: NavController, portfolioId: String, selectedSedeId: String) { // MODIFIED: Add portfolioId
    val viewModel: InventoryMovementsViewModel = viewModel(
        factory = InventoryMovementsViewModelFactory(portfolioId, selectedSedeId) // MODIFIED: Pass portfolioId
    )
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Movimientos de Inventario",
        navController = navController,
        portfolioId = portfolioId, // MODIFIED: Pass portfolioId to AppScaffold
        selectedSedeId = selectedSedeId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp)
        ) {
            // YA NO HAY TARJETA DE ENCABEZADO para Movimientos, ya que no hay lista para mostrar

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is InventoryMovementsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is InventoryMovementsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                    is InventoryMovementsUiState.Success -> {
                        // Este bloque teóricamente no se alcanzará a menos que simules datos
                        if (state.movements.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay movimientos registrados.", textAlign = TextAlign.Center)
                            }
                        } else {
                            // Esta parte renderizaría la lista si los datos estuvieran disponibles
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.movements, key = { it.id }) { movement ->
                                    InventoryMovementItem(movement = movement)
                                }
                            }
                        }
                    }
                    is InventoryMovementsUiState.FeatureUnavailable -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "La funcionalidad para listar movimientos de inventario no está disponible en el backend actual.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryMovementItem(movement: InventoryTransactionResource) {
    // Este composable es ahora principalmente un marcador de posición para cuando los datos puedan existir eventualmente.
    // No se renderizará en el escenario actual del backend.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = movement.movementDate.format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center,
                color = BrownDark
            )
            Text(
                text = movement.type.name,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = BrownDark
            )
            Text(
                text = "${movement.quantity} ${movement.origin}", // Visualización simplificada
                modifier = Modifier.weight(1.3f),
                textAlign = TextAlign.Center,
                color = BrownDark
            )
        }
    }
}