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

// NO ES NECESARIO UN IMPORT EXPLICITO SI ESTA EN EL MISMO PAQUETE,
// PERO DEJARLO NO HACE DAÑO Y PUEDE AYUDAR AL IDE A RESOLVERLO.
// Si ya tienes un import para ProviderDetailViewModel, esta línea es redundante
// ya que la factory está en el mismo archivo/paquete.
// Si la factory estuviera en un subpaquete o archivo diferente, sí lo necesitarías.
// Por el momento, si tienes este import y sigue dando error, elimínalo y deja
// que el IDE lo resuelva automáticamente si está en el mismo paquete.
// Para ser explícitos y seguros, lo mantenemos como estaba si lo has copiado:
import com.example.icafe.features.contacts.presentation.provider.ProviderDetailViewModelFactory


@Composable
fun AddEditProviderScreen(navController: NavController, portfolioId: String, selectedSedeId: String, providerId: Long?) {
    val viewModel: ProviderDetailViewModel = viewModel(
        factory = ProviderDetailViewModelFactory(portfolioId, selectedSedeId, providerId)
    )
    val isEditMode = providerId != null
    val title = if (isEditMode) "Editar Proveedor" else "Agregar Proveedor"
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProviderEvent.ActionSuccess -> {
                    navController.navigate(Route.ProviderList.createRoute(portfolioId, selectedSedeId)) {
                        popUpTo(Route.ProviderList.createRoute(portfolioId, selectedSedeId)) { inclusive = true }
                    }
                }
                is ProviderEvent.ActionError -> { /* Mostrar error */ }
            }
        }
    }

    AppScaffold(title = title, navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId) {
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