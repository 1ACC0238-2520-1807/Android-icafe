package com.example.icafe.features.contacts.presentation.provider

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
import com.example.icafe.features.contacts.data.network.ProviderResource
import com.example.icafe.features.home.presentation.scaffold.AppScaffold
import com.example.icafe.ui.theme.*

@Composable
fun ProviderListScreen(navController: NavController, portfolioId: String) {
    val viewModel: ProviderListViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = "Proveedores",
        navController = navController,
        portfolioId = portfolioId
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(16.dp)
        ) {
            Button(
                onClick = { navController.navigate(Route.AddProvider.createRoute(portfolioId)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OliveGreen)
            ) {
                Text("Agregar Proveedor", fontSize = 18.sp, color = Color.White)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BrownMedium)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "NOMBRE",
                        modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TELÉFONO",
                        modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        textAlign = TextAlign.Center, color = Color.White, fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is ProviderListUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is ProviderListUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
                    is ProviderListUiState.Success -> {
                        if (state.providers.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay proveedores registrados.", textAlign = TextAlign.Center)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.providers) { provider ->
                                    ProviderItem(provider = provider) {
                                        navController.navigate(Route.ProviderDetail.createRoute(portfolioId, provider.id))
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
fun ProviderItem(provider: ProviderResource, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Nombre y Teléfono
        Row(modifier = Modifier.weight(1f)) {
            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = LightGrayBackground) {
                Text(text = provider.nameCompany, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), fontSize = 16.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), color = LightGrayBackground) {
                Text(text = provider.phoneNumber, modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), fontSize = 16.sp, maxLines = 1)
            }
        }
        // Botón
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OliveGreen),
        ) {
            Text("Ver más", color = Color.White)
        }
    }
}