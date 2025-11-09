package com.example.icafe.features.sede.presentation.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.R
import com.example.icafe.features.contacts.presentation.employee.ConfirmationDialog
import com.example.icafe.features.contacts.presentation.employee.StyledTextField
import com.example.icafe.ui.theme.*

// REMOVED: Redundant import for AddEditSedeViewModelFactory (it's in the same package)
// REMOVED: Unnecessary import for SedeSelectionViewModelFactory


@Composable
fun AddEditSedeScreen(
    navController: NavController,
    sedeId: String? // "new" for add, actual ID for edit
) {
    val portfolioId = navController.previousBackStackEntry
        ?.arguments?.getString("portfolioId") ?: "1" // Attempt to get from previous route


    val viewModel: AddEditSedeViewModel = viewModel(factory = AddEditSedeViewModel.Factory(sedeId, portfolioId))
    val isEditMode = sedeId != "new" && sedeId != null
    val title = if (isEditMode) "Editar Sede" else "Nueva Sede"
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddEditSedeEvent.ActionSuccess -> {
                    navController.popBackStack()
                }
                is AddEditSedeEvent.ActionError -> {
                    // Show error, e.g., via Snackbar
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoicafe),
            contentDescription = "iCafe Logo",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "iCafe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = ColorIcafe,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BrownMedium.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Coffee, contentDescription = "Cafeteria Icon",
                    modifier = Modifier.size(48.dp).background(Peach, RoundedCornerShape(12.dp)).padding(8.dp),
                    tint = BrownDark
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            StyledTextField(label = "Nombre", value = viewModel.name, onValueChange = { viewModel.name = it })
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(label = "Dirección", value = viewModel.address, onValueChange = { viewModel.address = it })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showSaveDialog = true },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = BrownMedium)
        ) {
            Text(if (isEditMode) "Guardar Cambios" else "Nueva Sede", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Regresar", style = TextStyle(textDecoration = TextDecoration.Underline), color = Color.Gray)
        }
    }

    if (showSaveDialog) {
        ConfirmationDialog(
            title = if(isEditMode) "¿Quiere guardar sus cambios?" else "¿Quiere agregar esta sede?",
            onConfirm = {
                viewModel.saveSede()
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false },
            backgroundColor = BrownMedium,
            textColor = Color.White
        )
    }
}