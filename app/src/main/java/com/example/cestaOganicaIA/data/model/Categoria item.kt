package com.example.cestaOganicaIA.data.model

import androidx.annotation.DrawableRes

/**
 * Producto que se muestra en el catálogo.
 * Actualmente en memoria; cuando llegue Firebase se reemplaza
 * por un documento de la colección "productos" sin cambiar la UI.
 */
data class CatalogoItem(
    val nombre: String,
    val precio: Int,          // en pesos CLP
    val descripcion: String,
    val stock: Int,
    @DrawableRes val imagenResId: Int,
    val categoria: String
)
