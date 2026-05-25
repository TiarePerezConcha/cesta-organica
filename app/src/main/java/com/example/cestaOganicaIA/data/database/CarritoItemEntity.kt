package com.example.cestaOganicaIA.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito_items")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val nombreProducto: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val imagenResId: Int
)
