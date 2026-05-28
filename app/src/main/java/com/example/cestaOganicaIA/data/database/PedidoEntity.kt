package com.example.cestaOganicaIA.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para Pedidos (Historial) en Room.
 */
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val idLocal: Int = 0,
    val id: String = "",            // ID Firestore
    val ordenId: String = "",       
    val usuarioId: String = "",
    val nombreProducto: String = "",
    val precioUnitario: Int = 0,
    val cantidad: Int = 0,
    val total: Int = 0,
    val fechaEntrega: String = "",
    val direccionEntrega: String = "",
    val fechaPedido: String = "",
    val estado: String = "Confirmado",
    val imagenResId: Int = 0,
    val nombreContacto: String = "",
    val correoContacto: String = "",
    val telefonoContacto: String = ""
)
