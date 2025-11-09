package com.example.icafe.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.ui.theme.OffWhiteBackground


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    portfolioId: String, // MODIFIED: Added portfolioId
    selectedSedeId: String,
    productId: Long?
) {
    val viewModel: AddEditProductViewModel = viewModel(factory = AddEditProductViewModel.Factory(portfolioId, selectedSedeId, productId))
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    AppScaffold(
        title = if (productId == null) "Agregar Producto" else "Editar Producto",
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
            when (uiState) {
                is AddEditProductUiState.Loading, is AddEditProductUiState.LoadingSupplyItems -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                is AddEditProductUiState.Success, is AddEditProductUiState.Error, is AddEditProductUiState.Editing -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f), // Occupy available space, allowing scrolling
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StyledTextField(
                            label = "Nombre del Producto",
                            value = formState.name,
                            onValueChange = { viewModel.updateName(it) }
                        )
                        StyledTextField(
                            label = "Precio de Costo",
                            value = formState.costPrice,
                            onValueChange = { viewModel.updateCostPrice(it) },
                            keyboardType = KeyboardType.Number
                        )
                        StyledTextField(
                            label = "Margen de Ganancia (%)",
                            value = formState.profitMargin,
                            onValueChange = { viewModel.updateProfitMargin(it) },
                            keyboardType = KeyboardType.Number
                        )

                        Text("Ingredientes:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Selected Ingredients List
                        if (formState.selectedIngredients.isEmpty()) {
                            Text("No hay ingredientes añadidos.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 200.dp) // Limit height for scrolling
                            ) {
                                items(formState.selectedIngredients, key = { it.supplyItemId }) { ingredient ->
                                    IngredientDisplayItem(
                                        ingredient = ingredient,
                                        onQuantityChange = { newQty ->
                                            formState.availableSupplyItems.find { it.id == ingredient.supplyItemId }?.let { supplyItem ->
                                                viewModel.addOrUpdateIngredient(supplyItem, newQty)
                                            }
                                        },
                                        onRemove = { viewModel.removeIngredientFromForm(ingredient.supplyItemId) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Add Ingredient Section
                        AddIngredientSection(
                            availableSupplyItems = formState.availableSupplyItems,
                            onAddIngredient = { supplyItem, quantity -> viewModel.addOrUpdateIngredient(supplyItem, quantity) }
                        )
                    }

                    Button(
                        onClick = { viewModel.saveProduct() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = uiState !is AddEditProductUiState.Loading // Disable button when loading
                    ) {
                        if (uiState is AddEditProductUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (productId == null) "Guardar Producto" else "Actualizar Producto")
                        }
                    }

                    // Display success or error messages
                    if (uiState is AddEditProductUiState.Success) {
                        Text((uiState as AddEditProductUiState.Success).message, color = MaterialTheme.colorScheme.primary)
                        // Trigger navigation back after a short delay or user acknowledgment
                        LaunchedEffect(Unit) {
                            navController.popBackStack() // Navigate back on success
                        }
                    } else if (uiState is AddEditProductUiState.Error) {
                        Text((uiState as AddEditProductUiState.Error).message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientSection(
    availableSupplyItems: List<SupplyItemResource>,
    onAddIngredient: (SupplyItemResource, String) -> Unit
) {
    var selectedSupplyItem by remember { mutableStateOf<SupplyItemResource?>(null) }
    var quantityText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Añadir nuevo ingrediente:", style = MaterialTheme.typography.titleSmall)

        // Dropdown for selecting Supply Item
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedSupplyItem?.name ?: "Seleccionar Insumo",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableSupplyItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.name) },
                        onClick = {
                            selectedSupplyItem = item
                            expanded = false
                        }
                    )
                }
            }
        }

        StyledTextField(
            label = "Cantidad requerida",
            value = quantityText,
            onValueChange = { quantityText = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                selectedSupplyItem?.let { item ->
                    if (quantityText.toDoubleOrNull() != null && quantityText.toDouble() > 0) {
                        onAddIngredient(item, quantityText)
                        selectedSupplyItem = null
                        quantityText = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedSupplyItem != null && quantityText.toDoubleOrNull() != null && quantityText.toDouble() > 0
        ) {
            Text("Añadir Ingrediente")
        }
    }
}

@Composable
fun IngredientDisplayItem(
    ingredient: ProductIngredientForm,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(8.dp)) {
            Text(text = ingredient.supplyItemName, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Unidad: ${ingredient.unit}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {


            Text(text = ingredient.unit, style = MaterialTheme.typography.bodySmall)

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar ingrediente", tint = Color.Red)
            }
        }
    }
}