package com.example.icafe.features.sede.presentation.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.icafe.R
import com.example.icafe.ui.theme.ICafeTheme

@Composable
fun AddSedeScreen(
    navController: NavController,
    onAddSede: (nombre: String, ubicacion: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8D6E63).copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Coffee, contentDescription = "Cafeteria Icon",
                    modifier = Modifier.size(48.dp).background(Color(0xFFF5E0D8), RoundedCornerShape(12.dp)).padding(8.dp),
                    tint = Color(0xFF6D4C41)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Mi cafeteria", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Nombre", fontWeight = FontWeight.Bold, color = Color(0xFF8F4C32), modifier = Modifier.padding(start = 12.dp, bottom = 4.dp))
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedContainerColor = Color.LightGray.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Ej: Sede Centro") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ubicacion", fontWeight = FontWeight.Bold, color = Color(0xFF8F4C32), modifier = Modifier.padding(start = 12.dp, bottom = 4.dp))
            OutlinedTextField(
                value = ubicacion, onValueChange = { ubicacion = it }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                    focusedContainerColor = Color.LightGray.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Ej: Calle Falsa 123") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onAddSede(nombre, ubicacion)
                navController.popBackStack()
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
        ) {
            Text("Nueva Sede", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Regresar", style = TextStyle(textDecoration = TextDecoration.Underline), color = Color.Gray)
        }
    }
}