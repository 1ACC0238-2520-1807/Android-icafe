package com.example.icafe.features.finances.presentation.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.finances.presentation.ConfirmationDialog
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.products.data.network.ProductResource
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll // Importar verticalScroll

// --- Composable de la UI (AddSaleScreen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: AddSaleViewModel = viewModel(factory = AddSaleViewModel.Factory(portfolioId, selectedSedeId))
    val uiState by viewModel.uiState.collectAsState()
    val availableProducts by viewModel.availableProducts.collectAsState()
    val selectedSaleItems = viewModel.selectedSaleItems

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showProductPicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AddSaleUiState.Success) {
            navController.popBackStack()
        }
        // Puedes añadir lógica para mostrar un Snackbar en caso de errores aquí
        // if (uiState is AddSaleUiState.Error) { /* show snackbar */ }
    }
    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            // Aquí se podría mostrar un Snackbar para mensajes como "Producto ya agregado"
            // Para una aplicación real, integrarías un SnackbarHostState
            // Por ejemplo: snackbarHostState.showSnackbar(message)
        }
    }

    AppScaffold(
        title = "Registrar Venta",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(scaffoldInnerPadding)
                .padding(horizontal = 16.dp)
        ) {
            when (uiState) {
                is AddSaleUiState.Loading, is AddSaleUiState.Processing -> {
                    CircularProgressIndicator(color = OliveGreen, modifier = Modifier.align(Alignment.Center))
                }
                is AddSaleUiState.Error -> Text((uiState as AddSaleUiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is AddSaleUiState.ReadyForInput, is AddSaleUiState.Success -> {
                    val scrollState = rememberScrollState() // Crear un estado de desplazamiento
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OffWhiteBackground)
                            .verticalScroll(scrollState) // Habilitar el desplazamiento vertical
                            .padding(bottom = 16.dp) // Añadir un padding inferior para que el último elemento no esté pegado al borde
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = OliveGreen)
                        ) {
                            Text(
                                text = "Nueva Venta",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        StyledTextField(label = "ID Cliente", value = viewModel.customerId, onValueChange = { viewModel.customerId = it }, keyboardType = KeyboardType.Number)
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextField(label = "Notas (opcional)", value = viewModel.notes, onValueChange = { viewModel.notes = it })
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Productos:", style = MaterialTheme.typography.titleMedium, color = BrownDark)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Lista de productos de la venta (items seleccionados)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp)
                                .background(LightGrayBackground, RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (selectedSaleItems.isEmpty()) {
                                item {
                                    Text("No hay productos añadidos.", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray)
                                }
                            } else {
                                itemsIndexed(selectedSaleItems) { index, item ->
                                    SaleItemInput(
                                        item = item,
                                        onQuantityChange = { qty -> viewModel.updateSaleItemQuantity(index, qty) },
                                        onRemove = { viewModel.removeSaleItem(index) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para añadir un nuevo producto
                        Button(
                            onClick = { showProductPicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BrownMedium),
                            enabled = !viewModel.isSubmitting
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Producto", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Añadir Producto", color = Color.White)
                        }

                        // Eliminado: Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(16.dp)) // Añadir un Spacer fijo si se necesita un poco de separación

                        // Total de la venta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:", style = MaterialTheme.typography.headlineSmall, color = BrownDark)
                            Text("S/. ${String.format("%.2f", viewModel.totalAmount)}", style = MaterialTheme.typography.headlineSmall, color = BrownDark)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón para registrar la venta
                        Button(
                            onClick = { showConfirmationDialog = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                            enabled = viewModel.customerId.isNotBlank() && selectedSaleItems.isNotEmpty() &&
                                    !viewModel.isSubmitting
                        ) {
                            if (viewModel.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Registrar Venta", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Diálogo de confirmación para registrar la venta
            if (showConfirmationDialog) {
                ConfirmationDialog(
                    title = "¿Quiere registrar esta venta?",
                    onConfirm = {
                        viewModel.registerSale()
                        showConfirmationDialog = false
                    },
                    onDismiss = { showConfirmationDialog = false },
                    backgroundColor = OliveGreen,
                    textColor = Color.White,
                    isConfirmEnabled = !viewModel.isSubmitting
                )
            }

            // Diálogo para seleccionar productos
            if (showProductPicker) {
                ProductPicker(
                    products = availableProducts,
                    onProductSelected = { product ->
                        viewModel.addProductToSale(product)
                        showProductPicker = false
                    },
                    onDismiss = { showProductPicker = false }
                )
            }
        }
    }
}

// ProductPicker y SaleItemInput no necesitan cambios
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPicker(
    products: List<ProductResource>,
    onProductSelected: (ProductResource) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = OffWhiteBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seleccionar Producto", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(products) { product ->
                        ListItem(
                            headlineContent = { Text(product.name) },
                            trailingContent = { Text("S/. ${String.format("%.2f", product.salePrice)}") },
                            modifier = Modifier
                                .clickable { onProductSelected(product) }
                                .padding(vertical = 4.dp)
                        )
                        Divider()
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
fun SaleItemInput(
    item: SaleItemForm,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightGrayBackground, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, fontWeight = FontWeight.Medium, color = BrownDark)
            Text("S/. ${String.format("%.2f", item.unitPrice.toDoubleOrNull() ?: 0.0)}", fontSize = 12.sp, color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Cantidad:", fontSize = 14.sp, color = BrownDark)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = item.quantity,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } || newValue.isBlank()) {
                        onQuantityChange(newValue)
                    }
                },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.RemoveCircle, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}