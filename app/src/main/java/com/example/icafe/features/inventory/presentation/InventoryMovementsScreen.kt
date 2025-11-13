package com.example.icafe.features.inventory.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.InventoryTransactionResource
import com.example.icafe.features.inventory.data.network.TransactionType 
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.ui.theme.* 
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryMovementsScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: InventoryMovementsViewModel = viewModel(
        factory = InventoryMovementsViewModelFactory(portfolioId, selectedSedeId)
    )
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Movimientos de Inventario",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding -> // Este es el padding del AppScaffold (TopBar y BottomBar)
        val layoutDirection = LocalLayoutDirection.current // Obtener la dirección del layout

        // Contenido principal de la pantalla, sin el Scaffold anidado
        when (val state = uiState) {
            is InventoryMovementsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OffWhiteBackground)
                        .padding(scaffoldInnerPadding), // Aplicar padding del AppScaffold
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OliveGreen)
                }
            }
            is InventoryMovementsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OffWhiteBackground)
                        .padding(scaffoldInnerPadding), // Aplicar padding del AppScaffold
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { viewModel.loadMovements() },
                            colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }
            is InventoryMovementsUiState.Success -> {
                if (state.movements.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OffWhiteBackground)
                            .padding(scaffoldInnerPadding), // Aplicar padding del AppScaffold
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay movimientos registrados para esta sede.",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OffWhiteBackground) // Fondo principal para el contenido
                            .padding(
                                top = 16.dp, // Reducir el padding superior a un valor fijo más pequeño
                                start = scaffoldInnerPadding.calculateStartPadding(layoutDirection) + 16.dp, // Añadir padding horizontal
                                end = scaffoldInnerPadding.calculateEndPadding(layoutDirection) + 16.dp,   // Añadir padding horizontal
                                bottom = scaffoldInnerPadding.calculateBottomPadding() // Mantener el padding inferior del Scaffold
                            ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Columna izquierda para la lista de movimientos
                        Column(modifier = Modifier.weight(0.4f)) {
                            Text(
                                text = "Movimientos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrownDark,
                                modifier = Modifier.padding(bottom = 8.dp) // Mantener un pequeño padding aquí
                            )
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = BrownMedium)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(state.movements, key = { it.id }) { movement ->
                                        MovementListItem(
                                            movement = movement,
                                            isSelected = movement == state.selectedMovement,
                                            onClick = { viewModel.selectMovement(movement) }
                                        )
                                    }
                                }
                            }
                        }

                        // Columna derecha para el detalle del movimiento seleccionado
                        Column(modifier = Modifier.weight(0.6f)) {
                            Text(
                                text = state.selectedMovement?.let { "Movimiento ${it.id}" } ?: "Seleccione un movimiento",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrownDark,
                                modifier = Modifier.padding(bottom = 8.dp) // Mantener un pequeño padding aquí
                            )
                            state.selectedMovement?.let { selectedMovement ->
                                MovementDetailCard(
                                    movement = selectedMovement,
                                    supplyItemDetails = state.supplyItemDetails
                                )
                            } ?: run {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(LightGrayBackground, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay movimiento seleccionado", color = Color.Gray)
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
fun MovementListItem(
    movement: InventoryTransactionResource,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Peach else LightGrayBackground
    val contentColor = if (isSelected) BrownDark else Color.Black
    val indicatorColor = when (movement.type) {
        TransactionType.ENTRADA -> OliveGreen
        TransactionType.SALIDA -> RedWarning
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Movimiento ${movement.id}",
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(indicatorColor, CircleShape)
            )
        }
    }
}

@Composable
fun MovementDetailCard(movement: InventoryTransactionResource, supplyItemDetails: Map<Long, SupplyItemResource>) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilitar scroll vertical para el contenido del detalle
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailField("Identificador", movement.id.toString())
            DetailField("Origen", movement.origin)

            val supplyItem = supplyItemDetails[movement.supplyItemId]
            DetailField("Insumo", supplyItem?.name ?: "Desconocido")

            Column {
                Text(
                    text = "Tipo de Movimiento:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val demandText = movement.type.name
                    val demandColor = when (movement.type) {
                        TransactionType.ENTRADA -> OliveGreen
                        TransactionType.SALIDA -> RedWarning
                    }

                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = demandColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = demandText,
                            color = demandColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", movement.quantity)} ${supplyItem?.unit ?: ""}",
                        color = BrownDark,
                        fontSize = 16.sp
                    )
                }
            }

            DetailField("Fecha", movement.movementDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())))
        }
    }
}

@Composable
fun DetailField(label: String, value: String) {
    Column {
        Text(
            text = label + ":",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = OffWhiteBackground)
        ) {
            Text(
                text = value,
                color = BrownDark,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
