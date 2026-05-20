package com.example.cestaOganicaIA.data.model

data class Resena(
    val id: Int,// ID único de la reseña
    val nombreProducto: String,   // Para saber a qué producto pertenece
    val idUsuario: Int,           // Para saber qué usuario la escribió
    val nombreUsuario: String,    // Para mostrar quién la escribió
    val calificacion: Int,        // Un número del 1 al 5
    val comentario: String,       // El texto de la reseña
    val fecha: String             // La fecha de la reseña
)
