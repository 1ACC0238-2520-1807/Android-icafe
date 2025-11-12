package com.example.icafe.features.contacts.presentation.provider

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.contacts.presentation.employee.ConfirmationDialog
import com.example.icafe.features.contacts.presentation.employee.DetailInfoRow
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.BrownMedium
import com.example.icafe.ui.theme.OffWhiteBackground

// NO ES NECESARIO UN IMPORT EXPLICITO SI ESTA EN EL MISMO PAQUETE.
// Para ser explícitos y seguros, lo mantenemos como estaba si lo has copiado:
import com.example.icafe.features.contacts.presentation.provider.ProviderDetailViewModelFactory


@Composable
fun ProviderDetailScreen(navController: NavController, portfolioId: String, selectedSedeId: String, providerId: Long) {
    val viewModel: ProviderDetailViewModel = viewModel(
        factory = ProviderDetailViewModelFactory(portfolioId, selectedSedeId, providerId)
    )
    val provider = viewModel.provider
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProviderEvent.ActionSuccess -> {
                    navController.navigate(Route.ProviderList.createRoute(portfolioId, selectedSedeId)) {
                        popUpTo(Route.ProviderList.createRoute(portfolioId, selectedSedeId)) { inclusive = true }
                    }
                }
                is ProviderEvent.ActionError -> { /* Mostrar Snackbar o Toast */ }
            }
        }
    }

    AppScaffold(title = "Ver Proveedor", navController = navController, portfolioId = portfolioId, selectedSedeId = selectedSedeId) {
        Box(modifier = Modifier.fillMaxSize().background(OffWhiteBackground)) {
            if (viewModel.isLoading && provider == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (provider != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrownMedium)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(provider.nameCompany, fontSize = 20.sp, color = Color.White, modifier = Modifier.weight(1f))
                            IconButton(onClick = { navController.navigate(Route.EditProvider.createRoute(portfolioId, selectedSedeId, providerId)) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    DetailInfoRow(label = "RUC:", value = provider.ruc)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(label = "Gmail:", value = provider.email)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailInfoRow(label = "Teléfono:", value = provider.phoneNumber)

                    // Nota: El campo "Insumo" no existe en el backend, por lo que no se muestra.

                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
                    ) {
                        Text("Eliminar", fontSize = 18.sp)
                    }
                }
            }

            if (showDeleteDialog) {
                ConfirmationDialog(
                    title = "¿Quiere eliminar este Proveedor?",
                    onConfirm = {
                        viewModel.deleteProvider()
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false },
                    backgroundColor = BrownMedium
                )
            }
        }
    }
}