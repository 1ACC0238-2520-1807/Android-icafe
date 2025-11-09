package com.example.icafe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect // Importar para SideEffect
import androidx.compose.ui.graphics.Color // Importar para Color si no está ya
import androidx.compose.ui.graphics.toArgb // Importar para toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView // Importar para LocalView
import androidx.core.view.WindowCompat // Importar para WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    // Añadir colores de fondo/superficie para un DarkMode más completo
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    error = Color(0xFFCF6679),
    onError = Color.Black,
)

// Light color scheme (Using your custom theme colors)
private val LightColorScheme = lightColorScheme(
    primary = ColorIcafe, // Tu color principal de la marca
    onPrimary = Color.White, // Color del texto sobre el primary
    primaryContainer = Peach, // Un color de fondo para elementos primarios
    onPrimaryContainer = BrownDark, // Color del texto sobre el primaryContainer

    secondary = BrownMedium, // Color secundario
    onSecondary = Color.White, // Color del texto sobre el secondary
    secondaryContainer = LightGrayBackground, // Un color de fondo para elementos secundarios
    onSecondaryContainer = BrownDark, // Color del texto sobre el secondaryContainer

    tertiary = OliveGreen, // Un color terciario
    onTertiary = Color.White, // Color del texto sobre el tertiary

    background = OffWhiteBackground, // Color de fondo general de las pantallas
    onBackground = BrownDark, // Color del texto sobre el background
    surface = OffWhiteBackground, // Color de la superficie de los componentes
    onSurface = BrownDark, // Color del texto sobre la surface
    surfaceVariant = LightGrayBackground, // Una variante de superficie (ej. para campos de texto)
    onSurfaceVariant = Color.Gray, // Color del texto sobre la surfaceVariant

    error = Color(0xFFB00020), // Color para errores
    onError = Color.White, // Color del texto sobre el error
)

@Composable
fun ICafeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Lógica para la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de que Typography esté definido en Typography.kt
        content = content
    )
}