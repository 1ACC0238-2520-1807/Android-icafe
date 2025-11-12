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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.features.inventory.data.network.UnitMeasureType // Ahora se llama InventoryDataModels.UnitMeasureType
import com.example.icafe.ui.theme.OffWhiteBackground
import kotlinx.coroutines.launch
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.OliveGreen

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

    AppScaffold(title = title, navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId) { scaffoldInnerPadding -> // Recibe el padding del AppScaffold
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            // ELIMINADO: modifier = Modifier.padding(scaffoldInnerPadding)
            // La Surface del AppScaffold ya maneja el padding de las barras superior e inferior.
            // Si se aplica de nuevo aquí, causa doble padding y los rectángulos blancos.
            modifier = Modifier.fillMaxSize() // Este Scaffold debe llenar el espacio que le dejó AppScaffold
        ) { innerScaffoldContentPadding -> // Este padding es para elementos *dentro* de este Scaffold, como un Snackbar.
            if (viewModel.isLoading && isEditMode && viewModel.supplyItem == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize() // Ocupa todo el espacio disponible dentro de este Scaffold
                        .background(OffWhiteBackground) // Aplica el fondo a toda esta Column
                        .padding(innerScaffoldContentPadding) // Aplica el padding del Scaffold interno (para snackbars, etc.)
                        .padding(horizontal = 16.dp) // Añade un padding horizontal para el contenido del formulario
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta de título similar a la pantalla de compras
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = OliveGreen)
                    ) {
                        Text(
                            text = if (isEditMode) "Editar Insumo" else "Nuevo Insumo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // Espaciador después de la tarjeta de título

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
                        value = viewModel.dateInputText,
                        onValueChange = { newDateString ->
                            viewModel.dateInputText = newDateString
                        },
                        label = { Text("Fecha de Vencimiento") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

                    Button(
                        onClick = { viewModel.saveItem() },
                        enabled = !viewModel.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp), // Aplicar forma redondeada
                        colors = ButtonDefaults.buttonColors( // Aplicar colores del tema
                            containerColor = OliveGreen,
                            contentColor = Color.White,
                            disabledContainerColor = BrownMedium.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White) // Color del progreso
                        } else {
                            Text("Guardar", color = Color.White, fontSize = 18.sp) // Color y tamaño del texto
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Espacio al final
                }
            }
        }
    }
}