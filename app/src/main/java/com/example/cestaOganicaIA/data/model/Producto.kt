package com.example.cestaOganicaIA.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de Producto para Room BBDD local.
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    val id: String = "",           
    val nombre: String = "",
    val precio: String = "",
    val stock: Int = 0,
    val descripcion: String = "",
    val imagenResId: Int = 0,      // Para imágenes en drawable
    val imagenUri: String? = null, // Para imágenes cargadas desde la galería
    val categoria: String = ""
)
