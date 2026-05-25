package com.example.cestaOganicaIA.data.database

/**
 * Modelo de Pedido para Firestore.
 * Colección principal: "pedidos"
 */
data class PedidoEntity(
    val id: String = "",            // ID del documento en Firestore
    val ordenId: String = "",       // ID de grupo para varios productos en una sola compra
    val usuarioId: String = "",     // Firebase UID o "invitado"
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

/**
 * Modelo de Ítem de Carrito para Firestore.
 * Sub-colección: "carritos/{userId}/items"
 */
data class CarritoItemEntity(
    val id: String = "",
    val usuarioId: String = "",
    val nombreProducto: String = "",
    val precioUnitario: Int = 0,
    val cantidad: Int = 0,
    val imagenResId: Int = 0
)

/**
 * Modelo de Favorito para Firestore.
 * Colección: "favoritos" (Document ID: userId_nombreProducto)
 */
data class FavoritoEntity(
    val usuarioId: String = "",
    val nombreProducto: String = ""
)
