package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importa todo de runtime para remember, mutableStateOf, getValue, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Importa viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.features.contacts.presentation.employee.ConfirmationDialog
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.ui.theme.*
import java.time.format.DateTimeFormatter
import androidx.compose.ui.window.Dialog // Importa Dialog desde ui.window

// <<<< ¡Se han eliminado las definiciones de UiState y ViewModel de aquí! >>>>
// Deben estar ÚNICAMENTE en AddPurchaseOrderViewModel.kt

// --- Composable de la UI (AddPurchaseOrderScreen) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseOrderScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    // Aquí se inicializa el ViewModel, el cual contiene el UiState y la lógica
    val viewModel: AddPurchaseOrderViewModel = viewModel(factory = AddPurchaseOrderViewModel.Factory(portfolioId, selectedSedeId))
    val uiState by viewModel.uiState.collectAsState()
    val availableProviders by viewModel.availableProviders.collectAsState()
    val availableSupplyItems by viewModel.availableSupplyItems.collectAsState()

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isPickingExpirationDate by remember { mutableStateOf(false) } // Para saber qué fecha se está seleccionando

    LaunchedEffect(uiState) {
        if (uiState is AddPurchaseOrderUiState.Success) {
            navController.popBackStack() // Regresar a la lista de órdenes de compra
        }
    }

    AppScaffold(
        title = "Registrar Compra",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp)
        ) {
            when (uiState) {
                is AddPurchaseOrderUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is AddPurchaseOrderUiState.Error -> Text((uiState as AddPurchaseOrderUiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is AddPurchaseOrderUiState.ReadyForInput, is AddPurchaseOrderUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp) // Espacio para el FAB
                            .background(OffWhiteBackground)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = OliveGreen)
                        ) {
                            Text(
                                text = "Nueva Compra",
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

                        // Selector de Proveedor
                        var expandedProvider by remember { mutableStateOf(false) } // Define el estado de expansión aquí
                        ExposedDropdownMenuBox(
                            expanded = expandedProvider,
                            onExpandedChange = { expandedProvider = !expandedProvider }
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedProvider?.nameCompany ?: "Seleccionar Proveedor *",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvider) }, // Usa el estado local
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedProvider, // Usa el estado local
                                onDismissRequest = { expandedProvider = false }
                            ) {
                                availableProviders.forEach { provider ->
                                    DropdownMenuItem(
                                        text = { Text(provider.nameCompany) },
                                        onClick = {
                                            viewModel.selectedProvider = provider
                                            expandedProvider = false // Cierra el menú al seleccionar
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Selector de Insumo
                        var expandedSupplyItem by remember { mutableStateOf(false) } // Define el estado de expansión aquí
                        ExposedDropdownMenuBox(
                            expanded = expandedSupplyItem,
                            onExpandedChange = { expandedSupplyItem = !expandedSupplyItem }
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedSupplyItem?.name ?: "Seleccionar Insumo *",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplyItem) }, // Usa el estado local
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSupplyItem, // Usa el estado local
                                onDismissRequest = { expandedSupplyItem = false }
                            ) {
                                availableSupplyItems.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.name) },
                                        onClick = {
                                            viewModel.selectedSupplyItem = item
                                            expandedSupplyItem = false // Cierra el menú al seleccionar
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        StyledTextField(label = "Cantidad *", value = viewModel.quantity, onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it == '.' } || newValue.isBlank()) { // Permite decimales
                                viewModel.quantity = newValue
                            }
                        }, keyboardType = KeyboardType.Number)
                        Spacer(modifier = Modifier.height(16.dp))
                        StyledTextField(label = "Precio Unitario *", value = viewModel.unitPrice, onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() || it == '.' } || newValue.isBlank()) { // Permite decimales
                                viewModel.unitPrice = newValue
                            }
                        }, keyboardType = KeyboardType.Number)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fecha de Compra
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fecha de Compra: ", color = BrownDark, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = viewModel.purchaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isPickingExpirationDate = false // Estamos seleccionando la fecha de compra
                                        showDatePicker = true
                                    }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Fecha de Vencimiento (opcional)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fecha de Vencimiento (opcional): ", color = BrownDark, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = viewModel.expirationDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.weight(1f),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isPickingExpirationDate = true // Estamos seleccionando la fecha de vencimiento
                                        showDatePicker = true
                                    }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        StyledTextField(label = "Notas (opcional)", value = viewModel.notes, onValueChange = { viewModel.notes = it })

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón para registrar la orden de compra
                        Button(
                            onClick = { showConfirmationDialog = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
                            enabled = viewModel.selectedProvider != null && viewModel.selectedSupplyItem != null &&
                                    viewModel.quantity.toDoubleOrNull() != null && viewModel.quantity.toDouble() > 0 &&
                                    viewModel.unitPrice.toDoubleOrNull() != null && viewModel.unitPrice.toDouble() > 0
                        ) {
                            Text("Registrar Compra", color = Color.White, fontSize = 18.sp)
                        }
                    }
                }
            }

            // Diálogo de confirmación para registrar la orden de compra
            if (showConfirmationDialog) {
                ConfirmationDialog(
                    title = "¿Quiere registrar esta compra?",
                    onConfirm = {
                        viewModel.registerPurchaseOrder()
                        showConfirmationDialog = false
                    },
                    onDismiss = { showConfirmationDialog = false },
                    backgroundColor = OliveGreen,
                    textColor = Color.White
                )
            }

            // Date Picker Dialog
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = (if (isPickingExpirationDate) viewModel.expirationDate else viewModel.purchaseDate)?.toEpochDay()?.times(24 * 60 * 60 * 1000L)
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                if (isPickingExpirationDate) {
                                    viewModel.expirationDate = selectedDate
                                } else {
                                    viewModel.purchaseDate = selectedDate
                                }
                            }
                            showDatePicker = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}