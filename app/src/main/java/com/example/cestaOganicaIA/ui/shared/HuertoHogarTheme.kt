package com.example.cestaOganicaIA.ui.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores de la marca Cesta Orgánica / Huerto Hogar
val VerdeHuerto  = Color(0xFF4CAF50)
val NaranjoHuerto = Color(0xFFFF9800)
val FondoCalido  = Color(0xFFFFF8F5)
val TextoOscuro  = Color(0xFF3A3A3A)
val RojoError    = Color(0xFFB00020)

private val HuertoHogarColorScheme = lightColorScheme(
    primary    = VerdeHuerto,
    onPrimary  = Color.White,
    secondary  = NaranjoHuerto,
    onSecondary = Color.White,
    surface    = FondoCalido,
    onSurface  = TextoOscuro,
    error      = RojoError,
    onError    = Color.White
)

@Composable
fun HuertoHogarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HuertoHogarColorScheme,
        content = content
    )
}
