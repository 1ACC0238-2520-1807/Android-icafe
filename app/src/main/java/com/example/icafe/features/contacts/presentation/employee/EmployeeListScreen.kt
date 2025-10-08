package com.example.icafe.features.contacts.presentation.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.icafe.core.Route
import com.example.icafe.features.contacts.data.network.EmployeeResource
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun EmployeeListScreen(navController: NavController, portfolioId: String) {
    val viewModel: EmployeeListViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Empleados",
        navController = navController,
        portfolioId = portfolioId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp)
        ) {
            // Botón "Agregar Empleado"
            Button(
                onClick = { navController.navigate(Route.AddEmployee.createRoute(portfolioId)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
            ) {
                Text("Agregar Empleado", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cabecera "NOMBRE"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrownMedium)
            ) {
                Text(
                    text = "NOMBRE",
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido principal (Lista, Carga o Error)
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is EmployeeListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is EmployeeListUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
                    is EmployeeListUiState.Success -> {
                        if (state.employees.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay empleados registrados.", textAlign = TextAlign.Center)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.employees) { employee ->
                                    EmployeeItem(employee = employee) {
                                        navController.navigate(Route.EmployeeDetail.createRoute(portfolioId, employee.id))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeItem(employee: EmployeeResource, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Espacio para el nombre
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = LightGrayBackground
        ) {
            Text(
                text = employee.name,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                fontSize = 16.sp
            )
        }
        // Botón "Ver más"
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("Ver más", color = Color.White)
        }
    }
}