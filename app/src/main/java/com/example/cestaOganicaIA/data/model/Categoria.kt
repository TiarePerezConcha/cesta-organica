package com.example.cestaOganicaIA.data.model

import androidx.compose.ui.graphics.vector.ImageVector

// Representa una categoría completa
data class Categoria(
    val nombre: String,
    val icono: ImageVector,
    val productos: List<CatalogoItem>
)
