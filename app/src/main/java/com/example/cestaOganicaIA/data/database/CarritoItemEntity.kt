package com.example.cestaOganicaIA.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito_items")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    val id: String = "",            // ID para compatibilidad Firestore
    val usuarioId: String,          // UID de Firebase
    val nombreProducto: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val imagenResId: Int
)
