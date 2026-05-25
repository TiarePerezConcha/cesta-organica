package com.example.cestaOganicaIA.data.model

/**
 * Modelo de reseña para Firestore.
 */
data class Resena(
    val id: String = "",          // Document ID en Firestore
    val nombreProducto: String = "",
    val idUsuario: String = "",   // Firebase UID
    val nombreUsuario: String = "",
    val calificacion: Int = 0,    // 1–5
    val comentario: String = "",
    val fecha: String = ""
)
