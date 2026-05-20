package com.example.cestaOganicaIA.data.model


// Representa a un usuario del sistema
data class Credential(
    val idUsuario: Int = 0,            // ID autogenerado (puede manejarlo Room más adelante)
    val nombre: String,                // Nombre completo
    val correo: String,                // Correo electrónico
    val usuario: String,               // Nombre de usuario único
    val telefono: String,              // Teléfono (9 dígitos)
    val direccion: String,             // Dirección de entrega
    val password: String               // Contraseña
) {

    companion object {
        // Usuario administrador por defecto
        val Admin = Credential(
            idUsuario = 1,
            nombre = "Administrador del Sistema",
            correo = "admin@duoc.cl",
            usuario = "admin",
            telefono = "000000000",
            direccion = "Sede Central DuocUC",
            password = "123"
        )
    }
}
