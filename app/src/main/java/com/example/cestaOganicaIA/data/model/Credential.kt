package com.example.cestaOganicaIA.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de usuario compatible con BBDD Local (Room) y Firebase.
 */
@Entity(tableName = "usuarios")
data class Credential(
    @PrimaryKey val uid: String = "", // ID único (Firebase UID)
    val nombre: String = "",
    val correo: String = "",
    val usuario: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val password: String = "",        // Necesario para login local
    val rol: String = "user"
) {
    companion object {
        val Admin = Credential(
            uid = "ADMIN_ID_001",
            nombre = "Administrador",
            correo = "admin@duoc.cl",
            usuario = "admin",
            telefono = "900000000",
            direccion = "Sede Central",
            password = "123",
            rol = "admin"
        )
    }
}
