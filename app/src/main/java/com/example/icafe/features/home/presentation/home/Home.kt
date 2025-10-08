package com.example.icafe.features.home.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee // MODIFICADO: Importar el ícono
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icafe.R
import com.example.icafe.ui.theme.ICafeTheme

// Un data class para representar una sede, facilitará el manejo de datos
data class Sede(val id: String, val name: String)

@Composable
fun Home(
    // Añadimos un callback para cuando se haga clic en una sede
    onSedeClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    // Datos de ejemplo
    val sedes = listOf(Sede("1", "Sede 1"), Sede("2", "Sede 2"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo y nombre de la app (ya lo tienes en tu diseño)
        Image(
            painter = painterResource(id = R.drawable.logoicafe),
            contentDescription = "iCafe Logo",
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "iCafe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6D4C41),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Card "Mi Cafeteria"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8D6E63).copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    // ANTES: painter = painterResource(id = R.drawable.ic_cafe_cup),
                    imageVector = Icons.Default.Coffee, // MODIFICADO: Usamos un ícono de Material
                    contentDescription = "Cafeteria Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF5E0D8), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    tint = Color(0xFF6D4C41)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Mi cafeteria",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de Sedes
        LazyColumn(
            modifier = Modifier.weight(1f), // Ocupa el espacio disponible
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(sedes) { sede ->
                SedeItem(sede = sede, onSedeClick = { onSedeClick(sede.id) }, onDeleteClick = { /* Lógica para borrar */ })
            }
        }

        // Botón "Añadir Sede"
        Button(
            onClick = { /* Lógica para añadir sede */ },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
        ) {
            Text("Añadir Sede", color = Color.White)
        }

        // Botón "Cerrar Sesión" al final
        TextButton(
            onClick = { onLogoutClick() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                "Cerrar Sesión",
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SedeItem(
    sede: Sede,
    onSedeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSedeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF947A6D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = sede.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Sede",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ICafeTheme {
        Home(onSedeClick = {}, onLogoutClick = {})
    }
}