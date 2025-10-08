package com.example.icafe.features.contacts.presentation.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun EmployeeDetailScreen(navController: NavController, portfolioId: String, employeeId: Long) {
    val viewModel: EmployeeDetailViewModel = viewModel()
    val employee = viewModel.employee
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EmployeeEvent.ActionSuccess -> {
                    navController.navigate(Route.EmployeeList.createRoute(portfolioId)) {
                        popUpTo(Route.EmployeeList.createRoute(portfolioId)) { inclusive = true }
                    }
                }
                is EmployeeEvent.ActionError -> { /* Mostrar Snackbar o Toast */ }
            }
        }
    }

    AppScaffold(title = "Ver más Empleado", navController = navController, portfolioId = portfolioId) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)) {
            if (viewModel.isLoading && employee == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (employee != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Cabecera con nombre y botón de editar
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(employee.name, fontSize = 20.sp, color = Color.White, modifier = Modifier.weight(1f))
                            IconButton(onClick = { navController.navigate(Route.EditEmployee.createRoute(portfolioId, employeeId)) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detalles
                    DetailInfoRow(label = "Rol", value = employee.role)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(label = "Gmail", value = employee.email)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(label = "Teléfono", value = employee.phoneNumber)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(label = "Sueldo", value = employee.salary)

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón Eliminar
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                    ) {
                        Text("Eliminar", fontSize = 18.sp)
                    }
                }
            }

            if (showDeleteDialog) {
                ConfirmationDialog(
                    title = "¿Quiere eliminar este empleado?",
                    onConfirm = {
                        viewModel.deleteEmployee()
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false },
                    backgroundColor = BrownMedium
                )
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, color = BrownDark, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LightGrayBackground
        ) {
            Text(text = value, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), fontSize = 16.sp)
        }
    }
}