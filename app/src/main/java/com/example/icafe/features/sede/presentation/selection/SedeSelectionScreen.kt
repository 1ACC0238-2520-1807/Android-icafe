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
import com.example.icafe.R
import com.example.icafe.ui.theme.ICafeTheme
import java.util.*

data class Sede(val id: String, val name: String)

private val sampleSedes = mutableStateListOf(
    Sede(id = UUID.randomUUID().toString(), name = "Sede 1")
)

@Composable
fun SedeSelectionScreen(
    onSedeClick: (String) -> Unit,
    onAddSedeClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var sedes by remember { mutableStateOf(sampleSedes.toList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var sedeToDelete by remember { mutableStateOf<Sede?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.logoicafe), contentDescription = "iCafe Logo", modifier = Modifier.size(80.dp))
        Text(text = "iCafe", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color(0xFF6D4C41), modifier = Modifier.padding(bottom = 32.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8D6E63).copy(alpha = 0.7f))
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Coffee, contentDescription = "Cafeteria Icon",
                    modifier = Modifier.size(48.dp).background(Color(0xFFF5E0D8), RoundedCornerShape(12.dp)).padding(8.dp),
                    tint = Color(0xFF6D4C41)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Mi cafeteria", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 24.dp)
        ) {
            items(sedes) { sede ->
                SedeItem(
                    sede = sede,
                    onSedeClick = { onSedeClick(sede.id) },
                    onDeleteClick = {
                        sedeToDelete = sede
                        showDeleteDialog = true
                    }
                )
            }
        }

        Button(
            onClick = onAddSedeClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
        ) {
            Text("Añadir Sede", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLogoutClick) {
            Text("Cerrar Sesión", style = TextStyle(textDecoration = TextDecoration.Underline), color = Color.Gray)
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                sedeToDelete?.let { sede ->
                    sampleSedes.remove(sede)
                    sedes = sampleSedes.toList()
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
fun SedeItem(sede: Sede, onSedeClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSedeClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF947A6D))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = sede.name, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Sede", tint = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF947A6D))) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "¿Quiere eliminar esta sede?", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = onConfirm, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7CCC8))) {
                        Text("Aceptar", color = Color(0xFF6D4C41))
                    }
                    Button(onClick = onDismiss, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text("Atrás", color = Color(0xFF6D4C41))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SedeSelectionScreenPreview() {
    ICafeTheme {
        SedeSelectionScreen({}, {}, {})
    }
}