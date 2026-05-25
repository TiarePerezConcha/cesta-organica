package com.example.cestaOganicaIA.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF388E3C),
    secondary = Color(0xFFF57C00),
    tertiary = Color(0xFF81C784),

    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(

    primary = Color(0xFF4CAF50),      // Verde principal
    secondary = Color(0xFFFF9800),   // Naranja
    tertiary = Color(0xFF81C784),    // Verde claro

    background = Color(0xFFFFF8F5),  // Fondo crema claro
    surface = Color(0xFFFFFFFF),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,

    onBackground = Color(0xFF3A3A3A),
    onSurface = Color(0xFF3A3A3A)
)

@Composable
fun HuertoHogarDefinitiveEditionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    // IMPORTANTE:
    // Lo dejamos en false para que Android
    // NO reemplace tus colores con Material You
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {

    val colorScheme = when {

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

            val context = LocalContext.current

            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}