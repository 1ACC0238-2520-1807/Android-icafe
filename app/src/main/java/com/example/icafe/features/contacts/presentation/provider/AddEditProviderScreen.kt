package com.example.icafe.features.contacts.presentation.provider

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.contacts.presentation.employee.ConfirmationDialog
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun AddEditProviderScreen(navController: NavController, portfolioId: String, providerId: Long?) {
    val viewModel: ProviderDetailViewModel = viewModel()
    val isEditMode = providerId != null
    val title = if (isEditMode) "Editar Proveedor" else "Agregar Proveedor"
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProviderEvent.ActionSuccess -> {
                    navController.navigate(Route.ProviderList.createRoute(portfolioId)) {
                        popUpTo(Route.ProviderList.createRoute(portfolioId)) { inclusive = true }
                    }
                }
                is ProviderEvent.ActionError -> { /* Mostrar error */ }
            }
        }
    }

    AppScaffold(title = title, navController = navController, portfolioId = portfolioId) {
        Box(modifier = Modifier.fillMaxSize().background(OffWhiteBackground)) {
            if (viewModel.isLoading && isEditMode) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isEditMode) Peach else OliveGreen)
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            color = if (isEditMode) BrownDark else Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    StyledTextField(label = "Nombre", value = viewModel.nameCompany, onValueChange = { viewModel.nameCompany = it })
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "RUC", value = viewModel.ruc, onValueChange = { viewModel.ruc = it }, keyboardType = KeyboardType.Number)
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Gmail", value = viewModel.email, onValueChange = { viewModel.email = it }, keyboardType = KeyboardType.Email)
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(label = "Teléfono", value = viewModel.phoneNumber, onValueChange = { viewModel.phoneNumber = it }, keyboardType = KeyboardType.Phone)

                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
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
                    title = if(isEditMode) "¿Quiere guardar sus cambios?" else "¿Quiere agregar este proveedor?",
                    onConfirm = {
                        viewModel.saveProvider()
                        showSaveDialog = false
                    },
                    onDismiss = { showSaveDialog = false },
                    backgroundColor = if (isEditMode) Peach else OliveGreen,
                    textColor = if(isEditMode) BrownDark else Color.White
                )
            }
        }
    }
}