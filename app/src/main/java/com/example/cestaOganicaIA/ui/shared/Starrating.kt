package com.example.cestaOganicaIA.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Dorado = Color(0xFFFFC107)

/** Muestra 5 estrellas en modo lectura */
@Composable
fun StarRating(calificacion: Int, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Row(modifier = modifier) {
        repeat(5) { i ->
            Icon(
                imageVector = if (i < calificacion) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (i < calificacion) Dorado else Color.Gray,
                modifier = Modifier.let { if (size == 16.dp) it else it }
            )
        }
    }
}

/** 5 estrellas interactivas para calificar */
@Composable
fun StarRatingInput(calificacion: Int, onCalificacionChange: (Int) -> Unit) {
    Row {
        repeat(5) { i ->
            IconButton(onClick = { onCalificacionChange(i + 1) }) {
                Icon(
                    imageVector = if (i < calificacion) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "${i + 1} estrellas",
                    tint = if (i < calificacion) Dorado else Color.Gray
                )
            }
        }
    }
}