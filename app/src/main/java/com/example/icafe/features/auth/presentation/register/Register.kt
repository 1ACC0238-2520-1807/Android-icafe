package com.example.icafe.features.auth.presentation.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icafe.R
import com.example.icafe.ui.theme.ColorIcafe
import com.example.icafe.ui.theme.ICafeTheme

@Composable
fun Register(
    viewModel: RegisterViewModel = viewModel(),
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit = {}
) {
    val uiState = viewModel.uiState

    LaunchedEffect(key1 = uiState) {
        if (uiState is RegisterUiState.Success) {
            onSubmit()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.logoicafe),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "iCafe",
                color = ColorIcafe,
                fontSize = 60.sp,
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        // --- CAMPO EMAIL ---
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null
                )
            },
            placeholder = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- CAMPO PASSWORD ---
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Contraseña") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- CAMPO CONFIRMAR PASSWORD ---
        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Confirmar Contraseña") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState is RegisterUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = { viewModel.register() },
            enabled = uiState !is RegisterUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorIcafe,
                contentColor = Color.White
            )
        ) {
            if (uiState is RegisterUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Registrarse")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Ya tengo una cuenta")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    ICafeTheme {
        Register(onSubmit = {}, onBackToLogin = {})
    }
}