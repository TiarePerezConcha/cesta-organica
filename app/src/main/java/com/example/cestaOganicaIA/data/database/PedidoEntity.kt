package com.example.cestaOganicaIA.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ordenId: Int,
    val usuarioId: Int,
    val nombreContacto: String,
    val correoContacto: String,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double,
    val imagenResId: Int,
    val direccionEntrega: String,
    val fechaPedido: String,
    val fechaEntrega: String,
    val estado: String
)
