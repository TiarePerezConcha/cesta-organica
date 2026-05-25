package com.example.cestaOganicaIA.ui.shared

/**
 * Rutas centralizadas de navegación.
 */
object AppRoutes {
    const val LOGIN             = "login"
    const val REGISTRO          = "registro"
    const val RECUPERAR_CLAVE   = "recuperar_clave"

    const val CATALOGO          = "catalogo"
    const val DETALLE_PRODUCTO  = "detalle/{nombre}"
    const val CARRITO           = "carrito"
    const val HISTORIAL         = "historial"
    const val FAVORITOS         = "favoritos"
    const val PERFIL            = "perfil"
    const val INFO              = "info"
    const val QR_SCANNER        = "qr_scanner"

    // Admin
    const val ADMIN_USUARIOS    = "admin_usuarios"
    const val ADMIN_STOCK       = "admin_stock"
    const val ADMIN_PEDIDOS     = "admin_pedidos"

    fun detalleProducto(nombre: String) = "detalle/$nombre"
}
