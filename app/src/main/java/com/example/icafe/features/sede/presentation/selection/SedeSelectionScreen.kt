package com.example.icafe.features.sede.presentation.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.R
import com.example.icafe.core.Route
import com.example.icafe.core.data.network.BranchResource
import com.example.icafe.core.data.network.TokenManager
import com.example.icafe.ui.theme.*

// REMOVED: Redundant import for SedeSelectionViewModelFactory (it's in the same package)


@Composable
fun SedeSelectionScreen(
    navController: NavController,
    portfolioId: String // Retrieved from Login
) {
    val viewModel: SedeSelectionViewModel = viewModel(
        factory = SedeSelectionViewModelFactory(portfolioId)
    )
    val uiState by viewModel.uiState.collectAsState()
    val sedes by viewModel.sedes.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var sedeToDelete by remember { mutableStateOf<BranchResource?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSedes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.logoicafe), contentDescription = "iCafe Logo", modifier = Modifier.size(80.dp))
        Text(text = "iCafe", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = ColorIcafe, modifier = Modifier.padding(bottom = 32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrownMedium.copy(alpha = 0.7f))
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Coffee, contentDescription = "Cafeteria Icon",
                    modifier = Modifier.size(48.dp).background(Peach, RoundedCornerShape(12.dp)).padding(8.dp),
                    tint = BrownDark
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Mi cafetería", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (val state = uiState) {
                is SedeSelectionUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is SedeSelectionUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                is SedeSelectionUiState.Success -> {
                    if (state.branches.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No hay sedes registradas.\nPresiona 'Añadir Sede' para agregar la primera.", textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 24.dp)
                        ) {
                            items(state.branches, key = { it.id }) { sede ->
                                SedeItem(
                                    sede = sede,
                                    onSedeClick = { navController.navigate(Route.Dashboard.createRoute(portfolioId, sede.id.toString())) },
                                    onEditClick = { navController.navigate(Route.AddEditSede.createRoute(sede.id.toString())) },
                                    onDeleteClick = {
                                        sedeToDelete = sede
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }


        Button(
            onClick = { navController.navigate(Route.AddEditSede.createRoute("new")) },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
        ) {
            Text("Añadir Sede", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            TokenManager.clearToken() // Clear token on logout
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Cerrar Sesión", style = TextStyle(textDecoration = TextDecoration.Underline), color = Color.Gray)
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                sedeToDelete?.let { sede ->
                    viewModel.deleteSede(sede.id.toString()) // Pass ID as String for delete API
                }
                showDeleteDialog = false
                sedeToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                sedeToDelete = null
            }
        )
    }
}

@Composable
fun SedeItem(sede: BranchResource, onSedeClick: () -> Unit, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSedeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrownMedium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = sede.name, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text(text = sede.address, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Sede", tint = Color.White.copy(alpha = 0.8f))
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Sede", tint = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

// DEFINED HERE: DeleteConfirmationDialog
@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = BrownMedium)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "¿Quiere eliminar esta sede?", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = onConfirm, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Peach)) {
                        Text("Aceptar", color = BrownDark)
                    }
                    Button(onClick = onDismiss, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text("Atrás", color = BrownDark)
                    }
                }
            }
        }
    }
}