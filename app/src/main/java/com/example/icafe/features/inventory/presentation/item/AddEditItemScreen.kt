package com.example.icafe.features.inventory.presentation.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.icafe.features.inventory.data.network.UnitMeasureType
import com.example.icafe.ui.theme.OffWhiteBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(navController: NavController, itemId: Long?) {
    val viewModel: ItemDetailViewModel = viewModel()
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

    AppScaffold(title = title, navController = navController, portfolioId = null) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            if (viewModel.isLoading && isEditMode) {
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

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.unit.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad de Medida") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            UnitMeasureType.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(unit.name) },
                                    onClick = {
                                        viewModel.unit = unit
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (!isEditMode) {
                        StyledTextField(label = "Cantidad Inicial", value = viewModel.initialQuantity, onValueChange = { viewModel.initialQuantity = it }, keyboardType = KeyboardType.Number)
                    }

                    StyledTextField(label = "Punto de Reorden", value = viewModel.reorderPoint, onValueChange = { viewModel.reorderPoint = it }, keyboardType = KeyboardType.Number)

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