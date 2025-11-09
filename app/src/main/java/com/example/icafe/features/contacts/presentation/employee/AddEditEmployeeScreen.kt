package com.example.icafe.features.contacts.presentation.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun AddEditEmployeeScreen(navController: NavController, portfolioId: String, selectedSedeId: String, employeeId: Long?) {
    // Asegúrate de que la clase EmployeeDetailViewModelFactory esté definida en este archivo
    // o en un archivo importable. En el código que te he dado, está definida al final de este mismo archivo.
    val viewModel: EmployeeDetailViewModel = viewModel(
        factory = EmployeeDetailViewModelFactory(portfolioId, selectedSedeId, employeeId)
    )
    val isEditMode = employeeId != null
    val title = if (isEditMode) "Editar Empleado" else "Agregar Empleado"
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EmployeeEvent.ActionSuccess -> {
                    navController.navigate(Route.EmployeeList.createRoute(portfolioId, selectedSedeId)) {
                        popUpTo(Route.EmployeeList.createRoute(portfolioId, selectedSedeId)) { inclusive = true }
                    }
                }
                is EmployeeEvent.ActionError -> { /* Show error */ }
            }
        }
    }

    AppScaffold(title = title, navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
        ) {
            if (viewModel.isLoading && isEditMode) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Title Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isEditMode) Peach else OliveGreen)
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            color = if (isEditMode) BrownDark else Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Form fields
                    StyledTextField(label = "Nombre", value = viewModel.name, onValueChange = { viewModel.name = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Rol", value = viewModel.role, onValueChange = { viewModel.role = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Gmail", value = viewModel.email, onValueChange = { viewModel.email = it }, keyboardType = KeyboardType.Email)
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Teléfono", value = viewModel.phoneNumber, onValueChange = { viewModel.phoneNumber = it }, keyboardType = KeyboardType.Phone)
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Sueldo", value = viewModel.salary, onValueChange = { viewModel.salary = it }, keyboardType = KeyboardType.Number)

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(isEditMode) Peach else OliveGreen)
                    ) {
                        Text(
                            text = if (isEditMode) "Guardar Cambios" else "Guardar",
                            color = if(isEditMode) BrownDark else Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            if (showSaveDialog) {
                ConfirmationDialog(
                    title = if(isEditMode) "¿Quiere guardar sus cambios?" else "¿Quiere agregar este empleado?",
                    onConfirm = {
                        viewModel.saveEmployee()
                        showSaveDialog = false
                    },
                    onDismiss = { showSaveDialog = false },
                    backgroundColor = if (isEditMode) Peach else OliveGreen,
                    textColor = if(isEditMode) BrownDark else Color.White
                )
            }
        }
    }
} // CIERRE DE AddEditEmployeeScreen

// --- Las siguientes funciones y clases DEBEN estar en este archivo, o en uno importado ---

@Composable
fun StyledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(text = label, fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGrayBackground,
                unfocusedContainerColor = LightGrayBackground,
                disabledContainerColor = LightGrayBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    backgroundColor: Color,
    textColor: Color = Color.White
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = title, color = textColor, fontSize = 20.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownDark.copy(alpha = 0.5f))
                    ) {
                        Text("Aceptar", color = Color.White)
                    }
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Atrás", color = BrownDark)
                    }
                }
            }
        }
    }
}


class EmployeeDetailViewModelFactory(
    private val portfolioId: String,
    private val selectedSedeId: String,
    private val employeeId: Long?
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmployeeDetailViewModel(
                savedStateHandle = androidx.lifecycle.SavedStateHandle().apply {
                    set("portfolioId", portfolioId)
                    set("selectedSedeId", selectedSedeId)
                    set("employeeId", employeeId?.toString())
                }
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}