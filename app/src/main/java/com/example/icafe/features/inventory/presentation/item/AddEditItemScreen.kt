package com.example.icafe.features.inventory.presentation.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.UnitMeasureType // Ahora se llama InventoryDataModels.UnitMeasureType
import com.example.icafe.ui.theme.OffWhiteBackground
import kotlinx.coroutines.launch
import com.example.icafe.features.contacts.data.network.ProviderResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(navController: NavController, portfolioId: String, selectedSedeId: String, itemId: Long?) {
    val viewModel: ItemDetailViewModel = viewModel(
        factory = ItemDetailViewModel.ItemDetailViewModelFactory(portfolioId, selectedSedeId, itemId) // Llamando a la Factory integrada
    )
    val isEditMode = itemId != null
    val title = if (isEditMode) "Editar Insumo" else "Agregar Insumo"

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ItemEvent.ActionSuccess -> navController.popBackStack()
                is ItemEvent.ActionError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }
    }

    AppScaffold(title = title, navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            if (viewModel.isLoading && isEditMode && viewModel.supplyItem == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OffWhiteBackground)
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StyledTextField(label = "Nombre del Insumo", value = viewModel.name, onValueChange = { viewModel.name = it })

                    var expandedUnit by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedUnit,
                        onExpandedChange = { expandedUnit = !expandedUnit }
                    ) {
                        OutlinedTextField(
                            value = viewModel.unit.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad de Medida") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expandedUnit,
                            onDismissRequest = { expandedUnit = false }
                        ) {
                            UnitMeasureType.values().forEach { unitEnum ->
                                DropdownMenuItem(
                                    text = { Text(unitEnum.name) },
                                    onClick = {
                                        viewModel.unit = unitEnum
                                        expandedUnit = false
                                    }
                                )
                            }
                        }
                    }

                    StyledTextField(label = "Precio Unitario", value = viewModel.unitPrice, onValueChange = { viewModel.unitPrice = it }, keyboardType = KeyboardType.Number)

                    StyledTextField(label = "Stock", value = viewModel.stock, onValueChange = { viewModel.stock = it }, keyboardType = KeyboardType.Number)

                    var expandedProvider by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedProvider,
                        onExpandedChange = { expandedProvider = !expandedProvider }
                    ) {
                        OutlinedTextField(
                            value = viewModel.selectedProvider?.nameCompany ?: "Seleccionar Proveedor",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Proveedor") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvider) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProvider,
                            onDismissRequest = { expandedProvider = false }
                        ) {
                            if (viewModel.availableProviders.isEmpty()) {
                                DropdownMenuItem(onClick = { /* Do nothing */ }, text = { Text("No hay proveedores disponibles") })
                            } else {
                                viewModel.availableProviders.forEach { provider ->
                                    DropdownMenuItem(
                                        text = { Text(provider.nameCompany) },
                                        onClick = {
                                            viewModel.selectedProvider = provider
                                            expandedProvider = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text("Fecha de Vencimiento (YYYY-MM-DD):", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = viewModel.dateInputText, // NEW: Use dateInputText from ViewModel
                        onValueChange = { newDateString ->
                            viewModel.dateInputText = newDateString // NEW: Update dateInputText directly
                        },
                        label = { Text("Fecha de Vencimiento") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { viewModel.saveItem() },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}