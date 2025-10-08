package com.example.icafe.features.inventory.presentation.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.ItemResource
import com.example.icafe.ui.theme.OffWhiteBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(navController: NavController) {
    val viewModel: ItemListViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Insumos",
        navController = navController,
        portfolioId = null
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Route.AddItem.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Insumo")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize().background(OffWhiteBackground)) {
                when (val state = uiState) {
                    is ItemListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is ItemListUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
                    is ItemListUiState.Success -> {
                        if (state.items.isEmpty()) {
                            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                                Text("No hay insumos registrados.\nPresiona '+' para agregar el primero.", textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text("Nombre", Modifier.weight(2f), fontWeight = FontWeight.Bold)
                                        Text("Cantidad", Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                                    }
                                    Divider(modifier = Modifier.padding(top = 8.dp))
                                }
                                items(state.items) { item ->
                                    ItemRow(item = item) {
                                        navController.navigate(Route.ItemDetail.createRoute(item.id))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemRow(item: ItemResource, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.nombre, Modifier.weight(2f), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${item.cantidadActual} ${item.unidadMedida.name.lowercase()}",
                Modifier.weight(1f),
                textAlign = TextAlign.End,
                color = if (item.requiereReabastecimiento) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}