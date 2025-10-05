package com.example.icafe.features.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icafe.R
import com.example.icafe.ui.theme.ColorIcafe
import com.example.icafe.ui.theme.ICafeTheme

@Composable
fun Register(onSubmit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Image(
                painterResource(R.drawable.logoicafe),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text="iCafe",
                color = ColorIcafe,
                fontSize = 60.sp,
                modifier = Modifier.padding(top = 25.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            leadingIcon ={
                Icon(
                    Icons.Default.Person,
                    contentDescription = null
                )
            },
            placeholder = {
                Text("Nombre de Cafeteria")
            }
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            leadingIcon ={
                Icon(
                    Icons.Default.Email,
                    contentDescription = null
                )
            },
            placeholder = {
                Text("Email")
            }
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            placeholder = {
                Text("Password")
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )
            }
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            placeholder = {
                Text("Confirme Password")
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null
                )
            }
        )

        Button(
            onClick = { onSubmit() },
            modifier = Modifier
                .width(280.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorIcafe,
                contentColor = Color.White
            )
        ) {
            Text("Login")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
            )
        }

        TextButton(
            onClick = {},
            colors = ButtonDefaults.textButtonColors(
                contentColor = ColorIcafe
            )
        ) {
            Text("ir a Login",
                style = TextStyle(
                    textDecoration = Underline
                ))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    ICafeTheme {
        Register{}
    }
}