package com.example.icafe.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.ItemResource
import com.example.icafe.features.inventory.data.network.UnitMeasureType
import com.example.icafe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productId: Long? = null
) {
    val viewModel: AddEditProductViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    // Inicializar el ViewModel con el ID del producto
    LaunchedEffect(productId) {
        viewModel.setProductId(productId)
    }

    val isEditMode = productId != null
    val screenTitle = if (isEditMode) "Editar Producto" else "Agregar Producto"

    AppScaffold(
        title = screenTitle,
        navController = navController,
        portfolioId = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is AddEditProductUiState.LoadingItems -> {
                    // Mostrar formulario básico mientras cargan los insumos
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            ProductBasicInfo(
                                name = formState.name,
                                onNameChange = viewModel::updateName,
                                category = formState.category,
                                onCategoryChange = viewModel::updateCategory,
                                portions = formState.portions,
                                onPortionsChange = viewModel::updatePortions,
                                steps = formState.steps,
                                onStepsChange = viewModel::updateSteps
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Insumos Disponibles",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrownDark
                                    )
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = OliveGreen, modifier = Modifier.size(24.dp))
                                    }
                                    Text(
                                        text = "Cargando insumos disponibles...",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Ingredientes Seleccionados",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BrownDark
                                    )
                                    Text(
                                        text = "No hay ingredientes seleccionados",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { viewModel.saveProduct() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                                enabled = false // Deshabilitado mientras cargan los insumos
                            ) {
                                Text(
                                    text = if (isEditMode) "Actualizar Producto" else "Crear Producto",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                is AddEditProductUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = OliveGreen)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Guardando producto...", color = Color.Gray)
                        }
                    }
                }
                is AddEditProductUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { navController.navigateUp() },
                                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
                            ) {
                                Text("Volver", color = Color.White)
                            }
                        }
                    }
                }
                is AddEditProductUiState.Success -> {
                    if (state.message.isNotEmpty()) {
                        // Mostrar mensaje de éxito y navegar de vuelta
                        LaunchedEffect(state.message) {
                            navController.navigateUp()
                        }
                    } else {
                        // Mostrar formulario cuando se cargan los insumos
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                ProductBasicInfo(
                                    name = formState.name,
                                    onNameChange = viewModel::updateName,
                                    category = formState.category,
                                    onCategoryChange = viewModel::updateCategory,
                                    portions = formState.portions,
                                    onPortionsChange = viewModel::updatePortions,
                                    steps = formState.steps,
                                    onStepsChange = viewModel::updateSteps
                                )
                            }

                            item {
                                AvailableItemsSection(
                                    availableItems = formState.availableItems,
                                    onAddComponent = viewModel::addComponent
                                )
                            }

                            item {
                                SelectedComponentsSection(
                                    selectedComponents = formState.selectedComponents,
                                    onRemoveComponent = viewModel::removeComponent
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { viewModel.saveProduct() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                                    enabled = formState.name.isNotBlank() && 
                                             formState.category.isNotBlank() && 
                                             formState.selectedComponents.isNotEmpty()
                                ) {
                                    Text(
                                        text = if (isEditMode) "Actualizar Producto" else "Crear Producto",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                is AddEditProductUiState.Editing -> {
                    // Mostrar formulario en modo edición
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            ProductBasicInfo(
                                name = formState.name,
                                onNameChange = viewModel::updateName,
                                category = formState.category,
                                onCategoryChange = viewModel::updateCategory,
                                portions = formState.portions,
                                onPortionsChange = viewModel::updatePortions,
                                steps = formState.steps,
                                onStepsChange = viewModel::updateSteps
                            )
                        }

                        item {
                            AvailableItemsSection(
                                availableItems = formState.availableItems,
                                onAddComponent = viewModel::addComponent
                            )
                        }

                        item {
                            SelectedComponentsSection(
                                selectedComponents = formState.selectedComponents,
                                onRemoveComponent = viewModel::removeComponent
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { viewModel.saveProduct() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                                enabled = formState.name.isNotBlank() && 
                                         formState.category.isNotBlank() && 
                                         formState.selectedComponents.isNotEmpty()
                            ) {
                                Text(
                                    text = if (isEditMode) "Actualizar Producto" else "Crear Producto",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductBasicInfo(
    name: String,
    onNameChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    portions: Int,
    onPortionsChange: (Int) -> Unit,
    steps: String,
    onStepsChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Información Básica",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nombre del Producto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = portions.toString(),
                    onValueChange = { 
                        val newValue = it.toIntOrNull() ?: 1
                        if (newValue > 0) onPortionsChange(newValue)
                    },
                    label = { Text("Porciones") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = steps,
                onValueChange = onStepsChange,
                label = { Text("Pasos de Preparación") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}

@Composable
fun AvailableItemsSection(
    availableItems: List<ItemResource>,
    onAddComponent: (ItemResource, Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Insumos Disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )

            Text(
                text = "Selecciona los insumos que necesitas para tu producto:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            if (availableItems.isEmpty()) {
                Text(
                    text = "No hay insumos disponibles. Primero registra algunos insumos.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                availableItems.forEach { item ->
                    ItemSelectorCard(
                        item = item,
                        onAddComponent = onAddComponent
                    )
                }
            }
        }
    }
}

@Composable
fun ItemSelectorCard(
    item: ItemResource,
    onAddComponent: (ItemResource, Double) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var showQuantityDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark
                )
                Text(
                    text = "Stock: ${item.cantidadActual} ${item.unidadMedida.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showQuantityDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Agregar", color = Color.White, fontSize = 12.sp)
            }
        }
    }

    if (showQuantityDialog) {
        QuantityInputDialog(
            item = item,
            onConfirm = { qty ->
                onAddComponent(item, qty)
                showQuantityDialog = false
            },
            onDismiss = { showQuantityDialog = false }
        )
    }
}

@Composable
fun QuantityInputDialog(
    item: ItemResource,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cantidad de ${item.nombre}") },
        text = {
            Column {
                Text("Ingresa la cantidad necesaria:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad (${item.unidadMedida.name.lowercase()})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Stock disponible: ${item.cantidadActual} ${item.unidadMedida.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    if (qty > 0) onConfirm(qty)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
            ) {
                Text("Agregar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun SelectedComponentsSection(
    selectedComponents: List<ProductComponentForm>,
    onRemoveComponent: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Ingredientes Seleccionados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BrownDark
            )

            if (selectedComponents.isEmpty()) {
                Text(
                    text = "No hay ingredientes seleccionados",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                selectedComponents.forEach { component ->
                    SelectedComponentCard(
                        component = component,
                        onRemove = { onRemoveComponent(component.itemId) }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedComponentCard(
    component: ProductComponentForm,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Peach)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = component.itemName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrownDark
                )
                Text(
                    text = "${component.quantity} ${component.unitMeasure.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remover",
                    tint = Color.Red
                )
            }
        }
    }
}