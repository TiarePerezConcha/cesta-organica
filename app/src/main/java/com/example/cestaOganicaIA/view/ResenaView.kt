package com.example.cestaOganicaIA.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cestaOganicaIA.data.model.Resena

// Composable para mostrar la calificación con estrellas
@Composable
fun CalificacionEnEstrellas(calificacion: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val icon = if (index < calificacion) Icons.Default.Star else Icons.Default.StarBorder
            val tint = if (index < calificacion) Color(0xFFFFC107) else Color.Gray
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Composable para mostrar una tarjeta de reseña individual
@Composable
fun CardResena(resena: Resena) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = resena.nombreUsuario,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                CalificacionEnEstrellas(calificacion = resena.calificacion)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resena.comentario,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resena.fecha,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
