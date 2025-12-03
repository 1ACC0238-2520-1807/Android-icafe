package com.example.icafe.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // <-- Importa itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.features.products.data.network.ProductStatus
import com.example.icafe.features.products.data.network.ProductIngredientResource // <-- Asegúrate de tener este import
import com.example.icafe.ui.theme.*
import android.util.Log // <-- Importa Log para depuración


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    portfolioId: String,
    selectedSedeId: String,
    productId: Long
) {
    val viewModel: ProductDetailViewModel = viewModel(
        factory = ProductDetailViewModel.Factory(portfolioId, selectedSedeId, productId)
    )
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showArchiveActivateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is ProductDetailUiState.Deleted) {
            navController.popBackStack()
        } else if (uiState is ProductDetailUiState.ProductActionSuccess) {
            // Consider adding a Toast/Snackbar here for user feedback
        }
    }

    AppScaffold(
        title = "Detalles del Producto",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(
                    top = 16.dp,
                    start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (val state = uiState) {
                    ProductDetailUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = OliveGreen)
                        }
                    }
                    is ProductDetailUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    is ProductDetailUiState.Success -> {
                        val product = state.product
                        ProductDetailCard("Nombre", product.name)
                        ProductDetailCard("Precio de Costo", "$${String.format("%.2f", product.costPrice)}")
                        ProductDetailCard("Precio de Venta", "$${String.format("%.2f", product.salePrice)}")
                        ProductDetailCard("Margen de Ganancia", "${String.format("%.2f", product.profitMargin)}%")
                        ProductDetailCard("Estado", product.status.name)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ingredientes:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        if (product.ingredients.isEmpty()) {
                            Text("No hay ingredientes para este producto.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 250.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                itemsIndexed( // <-- Usar itemsIndexed
                                    items = product.ingredients ?: emptyList(),
                                    key = { index: Int, _ -> index } // Usar índice como clave única ya que los IDs pueden repetirse
                                ) { index: Int, ingredient: ProductIngredientResource ->
                                    IngredientDisplayItemReadOnly(
                                        name = ingredient.name ?: "Desconocido",
                                        quantity = ingredient.quantity,
                                        unit = ingredient.unit ?: "unidad"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(Route.EditProduct.createRoute(portfolioId, selectedSedeId, productId))
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Producto", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Editar Producto", color = Color.White)
                            }

                            Button(
                                onClick = { showArchiveActivateDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (product.status == ProductStatus.ACTIVE) RedWarning else OliveGreen
                                )
                            ) {
                                if (product.status == ProductStatus.ACTIVE) {
                                    Icon(Icons.Default.Archive, contentDescription = "Archivar Producto", tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Archivar Producto", color = Color.White)
                                } else {
                                    Icon(Icons.Default.Unarchive, contentDescription = "Activar Producto", tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Activar Producto", color = Color.White)
                                }
                            }

                            Button(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Producto", tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Eliminar Producto", color = Color.White)
                            }
                        }
                    }
                    ProductDetailUiState.Deleted, ProductDetailUiState.ProductActionSuccess -> {
                        // Handled by LaunchedEffect
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirmar Eliminación") },
                    text = { Text("¿Estás seguro de que quieres eliminar este producto? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteProduct()
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (showArchiveActivateDialog && uiState is ProductDetailUiState.Success) {
                val product = (uiState as ProductDetailUiState.Success).product
                AlertDialog(
                    onDismissRequest = { showArchiveActivateDialog = false },
                    title = { Text(if (product.status == ProductStatus.ACTIVE) "Confirmar Archivo" else "Confirmar Activación") },
                    text = { Text(if (product.status == ProductStatus.ACTIVE) "¿Estás seguro de que quieres archivar este producto?" else "¿Estás seguro de que quieres activar este producto?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (product.status == ProductStatus.ACTIVE) {
                                    viewModel.archiveProduct()
                                } else {
                                    viewModel.activateProduct()
                                }
                                showArchiveActivateDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (product.status == ProductStatus.ACTIVE) RedWarning else OliveGreen
                            )
                        ) {
                            Text(if (product.status == ProductStatus.ACTIVE) "Archivar" else "Activar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showArchiveActivateDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun IngredientDisplayItemReadOnly(name: String, quantity: Double, unit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(text = "${String.format("%.2f", quantity)} $unit", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}