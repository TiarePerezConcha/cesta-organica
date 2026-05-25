package com.example.cestaOganicaIA.data.model

/**
 * Modelo de usuario para Firebase.
 * password no se guarda en Firestore por seguridad (se maneja en Firebase Auth).
 */
data class Credential(
    val uid: String = "",           // Firebase User ID
    val nombre: String = "",
    val correo: String = "",
    val usuario: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val rol: String = ""
) {
    companion object {
        val Admin = Credential(
            uid = "ADMIN_ID_PERMANENTE",
            nombre = "Administrador del Sistema",
            correo = "admin@duoc.cl",
            usuario = "admin",
            telefono = "900000000",
            direccion = "Sede Central DuocUC",
            rol = "admin"
        )
    }
}
