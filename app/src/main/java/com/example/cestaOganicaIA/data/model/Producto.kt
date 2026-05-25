package com.example.cestaOganicaIA.data.model

/**
 * Modelo de Producto para Firestore.
 */
data class Producto(
    val id: String = "",           // Document ID en Firestore
    val nombre: String = "",
    val precio: String = "",
    val stock: Int = 0,
    val descripcion: String = "",
    val imagenResId: Int = 0,       // Temporalmente ID local, idealmente URL de Firebase Storage
    val categoria: String = ""
)
