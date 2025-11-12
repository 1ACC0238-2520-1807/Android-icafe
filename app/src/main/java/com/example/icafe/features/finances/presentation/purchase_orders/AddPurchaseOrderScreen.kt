package com.example.icafe.features.finances.presentation.purchase_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.SupplyItemResource
import com.example.icafe.ui.theme.*
import java.time.format.DateTimeFormatter
import androidx.compose.ui.window.Dialog

import com.example.icafe.features.finances.presentation.ConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseOrderScreen(navController: NavController, portfolioId: String, selectedSedeId: String) {
    val viewModel: AddPurchaseOrderViewModel = viewModel(factory = AddPurchaseOrderViewModel.Factory(portfolioId, selectedSedeId))
    val uiState by viewModel.uiState.collectAsState()
    val availableProviders by viewModel.availableProviders.collectAsState()
    val availableSupplyItems by viewModel.availableSupplyItems.collectAsState()

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isPickingExpirationDate by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AddPurchaseOrderUiState.Success) {
            navController.popBackStack()
        }
        // Puedes añadir lógica para mostrar un Snackbar en caso de errores aquí
        // if (uiState is AddPurchaseOrderUiState.Error) { /* show snackbar */ }
    }

    AppScaffold(
        title = "Registrar Compra",
        navController = navController,
        portfolioId = portfolioId,
        selectedSedeId = selectedSedeId
    ) { scaffoldInnerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .verticalScroll(rememberScrollState())
                .padding(scaffoldInnerPadding) // Aplica el padding del AppScaffold
                .padding(horizontal = 16.dp) // Añade un padding horizontal para el contenido del formulario
        ) {
            // Manejo de los estados de carga y error a nivel de pantalla completa
            when (uiState) {
                is AddPurchaseOrderUiState.Loading, is AddPurchaseOrderUiState.Processing -> { // Incluye el nuevo estado Processing
                    // Si está cargando o procesando, centrar el indicador en la pantalla
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = OliveGreen)
                    }
                }
                is AddPurchaseOrderUiState.Error -> {
                    // Si hay un error, mostrar el mensaje de error centrado
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((uiState as AddPurchaseOrderUiState.Error).message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                }
                is AddPurchaseOrderUiState.ReadyForInput, is AddPurchaseOrderUiState.Success -> {
                    // Si está listo para la entrada o la operación fue exitosa, muestra el formulario
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

                    var expandedProvider by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedProvider,
                        onExpandedChange = { expandedProvider = !expandedProvider }
                    ) {
                        OutlinedTextField(
                            value = viewModel.selectedProvider?.nameCompany ?: "Seleccionar Proveedor *",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProvider) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProvider,
                            onDismissRequest = { expandedProvider = false }
                        ) {
                            availableProviders.forEach { provider ->
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
                    Spacer(modifier = Modifier.height(16.dp))

                    var expandedSupplyItem by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedSupplyItem,
                        onExpandedChange = { expandedSupplyItem = !expandedSupplyItem }
                    ) {
                        OutlinedTextField(
                            value = viewModel.selectedSupplyItem?.name ?: "Seleccionar Insumo *",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplyItem) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSupplyItem,
                            onDismissRequest = { expandedSupplyItem = false }
                        ) {
                            availableSupplyItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.name) },
                                    onClick = {
                                        viewModel.selectedSupplyItem = item
                                        expandedSupplyItem = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    StyledTextField(label = "Cantidad *", value = viewModel.quantity, onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' } || newValue.isBlank()) {
                            viewModel.quantity = newValue
                        }
                    }, keyboardType = KeyboardType.Number)
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Precio Unitario *", value = viewModel.unitPrice, onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == '.' } || newValue.isBlank()) {
                            viewModel.unitPrice = newValue
                        }
                    }, keyboardType = KeyboardType.Number)
                    Spacer(modifier = Modifier.height(16.dp))

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
                                    isPickingExpirationDate = false
                                    showDatePicker = true
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

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
                                    isPickingExpirationDate = true
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
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OliveGreen,
                            disabledContainerColor = BrownMedium.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        // Deshabilitar el botón si falta información o si ya se está enviando
                        enabled = viewModel.selectedProvider != null && viewModel.selectedSupplyItem != null &&
                                viewModel.quantity.toDoubleOrNull() != null && viewModel.quantity.toDouble() > 0 &&
                                viewModel.unitPrice.toDoubleOrNull() != null && viewModel.unitPrice.toDouble() > 0 &&
                                !viewModel.isSubmitting // Usar la bandera isSubmitting
                    ) {
                        if (viewModel.isSubmitting) { // Mostrar CircularProgressIndicator si se está enviando
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrar Compra", color = Color.White, fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Los diálogos (confirmación y DatePicker) se deben mostrar fuera del flujo principal de la UI
            if (showConfirmationDialog) {
                ConfirmationDialog(
                    title = "¿Quiere registrar esta compra?",
                    onConfirm = {
                        viewModel.registerPurchaseOrder()
                        showConfirmationDialog = false // Cerrar el diálogo inmediatamente al confirmar
                    },
                    onDismiss = { showConfirmationDialog = false },
                    backgroundColor = OliveGreen,
                    textColor = Color.White,
                    isConfirmEnabled = !viewModel.isSubmitting // Deshabilitar el botón de confirmar del diálogo
                )
            }

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