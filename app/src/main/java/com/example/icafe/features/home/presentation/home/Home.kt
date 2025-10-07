package com.example.icafe.features.home.presentation.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.icafe.core.Route
import com.example.icafe.features.auth.presentation.register.Register
import com.example.icafe.ui.theme.ICafeTheme

@Composable
fun Home(){
    Text(text = "soy home")
}


@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    ICafeTheme {
        Home()
    }
}