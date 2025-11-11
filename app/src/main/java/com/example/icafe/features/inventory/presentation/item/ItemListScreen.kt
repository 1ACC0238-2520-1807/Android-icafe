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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.features.inventory.data.network.SupplyItemWithCurrentStock
import com.example.icafe.ui.theme.LightGrayBackground
import com.example.icafe.ui.theme.OffWhiteBackground

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.platform.LocalLifecycleOwner

import com.example.icafe.features.home.presentation.scaffold.AppScaffold

// *** ELIMINAR ESTAS DEFINICIONES DE ESTE ARCHIVO:
// sealed class ItemListUiState {
//     object Loading : ItemListUiState()
//     data class Success(val items: List<SupplyItemWithCurrentStock>) : ItemListUiState()
//     data class Error(val message: String) : ItemListUiState()
// }
//
// class ItemListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() { ... }
// ***

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    // Aquí, simplemente usa el ViewModel, ya está importado
    val viewModel: ItemListViewModel = viewModel(
        factory = ItemListViewModel.ItemListViewModelFactory(portfolioId, selectedSedeId)
    )
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.loadItems() // Reload items when the screen is resumed
                }
            }
        })
    }


    AppScaffold(
        title = "Insumos",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Route.AddItem.createRoute(portfolioId, selectedSedeId)) }) {
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
                                items(state.items, key = { it.id }) { item ->
                                    ItemRow(item = item) {
                                        navController.navigate(Route.ItemDetail.createRoute(portfolioId, selectedSedeId, item.id))
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
fun ItemRow(item: SupplyItemWithCurrentStock, onClick: () -> Unit) {
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
            Text(item.name, Modifier.weight(2f), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${item.stock} ${item.unit.lowercase()}",
                Modifier.weight(1f),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}