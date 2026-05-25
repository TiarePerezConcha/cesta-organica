package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential
import kotlin.inc

object UserRepository {

    private val users = mutableListOf(Credential.Admin)
    private var nextId = (Credential.Admin.idUsuario + 1)


    private fun norm(s: String) = s.trim().lowercase()

    private fun isStrongPassword(s: String): Boolean {
        if (s.length < 8) return false
        val hasLetter = s.any { it.isLetter() }
        val hasDigit  = s.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private fun isValidEmail(s: String): Boolean {
        val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        return s.matches(emailRegex)
    }

    private fun isPhoneCL9(s: String): Boolean {
        val digits = s.filter { it.isDigit() }
        return digits.length == 9
    }

    //  Registro
    fun register(user: Credential): Result<Credential> {
        val uNorm = norm(user.usuario)
        val cNorm = norm(user.correo)

        if (users.any { norm(it.usuario) == uNorm }) {
            return Result.failure(IllegalArgumentException("El usuario ya existe"))
        }
        if (users.any { norm(it.correo) == cNorm }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese correo"))
        }

        if (!isValidEmail(user.correo)) return Result.failure(IllegalArgumentException("Correo inválido"))
        if (!isPhoneCL9(user.telefono)) return Result.failure(IllegalArgumentException("El teléfono debe tener 9 dígitos"))
        if (user.nombre.isBlank() || user.direccion.isBlank()) {
            return Result.failure(IllegalArgumentException("Nombre y dirección son obligatorios"))
        }
        if (!isStrongPassword(user.password)) {
            return Result.failure(IllegalArgumentException("La contraseña no cumple los requisitos"))
        }

        val withId = user.copy(
            idUsuario = nextId++,
            usuario = user.usuario.trim(),
            correo = user.correo.trim()
        )
        users += withId
        return Result.success(withId)
    }

    // Login
    fun login(usernameOrEmail: String, password: String): Result<Credential> {
        val q = norm(usernameOrEmail)
        val u = users.firstOrNull {
            (norm(it.usuario) == q || norm(it.correo) == q) && it.password == password
        }
        return if (u != null) Result.success(u)
        else Result.failure(IllegalArgumentException("Credenciales inválidas"))
    }

    // Olvidé contraseña
    fun updatePassword(usernameOrEmail: String, newPassword: String): Result<Unit> {
        val q = norm(usernameOrEmail)
        val idx = users.indexOfFirst {
            norm(it.usuario) == q || norm(it.correo) == q
        }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario/correo no existe"))
        if (!isStrongPassword(newPassword)) {
            return Result.failure(IllegalArgumentException("La nueva contraseña no cumple los requisitos"))
        }
        users[idx] = users[idx].copy(password = newPassword)
        return Result.success(Unit)
    }

    //  Actualización completa
    fun update(user: Credential): Result<Credential> {
        val idx = users.indexOfFirst { it.idUsuario == user.idUsuario }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario no encontrado"))

        val uNorm = norm(user.usuario)
        val cNorm = norm(user.correo)

        if (users.any { it.idUsuario != user.idUsuario && norm(it.usuario) == uNorm }) {
            return Result.failure(IllegalArgumentException("El nombre de usuario ya está en uso"))
        }
        if (users.any { it.idUsuario != user.idUsuario && norm(it.correo) == cNorm }) {
            return Result.failure(IllegalArgumentException("Ya existe una cuenta con ese correo"))
        }

        if (!isValidEmail(user.correo)) return Result.failure(IllegalArgumentException("Correo inválido"))
        if (!isPhoneCL9(user.telefono)) return Result.failure(IllegalArgumentException("El teléfono debe tener 9 dígitos"))
        if (user.nombre.isBlank() || user.direccion.isBlank()) {
            return Result.failure(IllegalArgumentException("Nombre y dirección son obligatorios"))
        }
        if (!isStrongPassword(user.password)) {
            return Result.failure(IllegalArgumentException("La contraseña no cumple los requisitos"))
        }

        val isAdmin = users[idx].idUsuario == Credential.Admin.idUsuario
        val finalUser = if (isAdmin) {
            user.copy(usuario = Credential.Admin.usuario) // proteger username admin
        } else {
            user.copy(
                usuario = user.usuario.trim(),
                correo = user.correo.trim()
            )
        }

        users[idx] = finalUser
        return Result.success(finalUser)
    }

    //Actualización de perfil
    fun updateProfile(idUsuario: Int, nombre: String, telefono: String, direccion: String): Result<Credential> {
        val idx = users.indexOfFirst { it.idUsuario == idUsuario }
        if (idx == -1) return Result.failure(IllegalArgumentException("Usuario no encontrado"))

        if (nombre.isBlank()) return Result.failure(IllegalArgumentException("El nombre es obligatorio"))
        if (direccion.isBlank()) return Result.failure(IllegalArgumentException("La dirección es obligatoria"))
        if (!isPhoneCL9(telefono)) return Result.failure(IllegalArgumentException("El teléfono debe tener 9 dígitos"))

        val cur = users[idx]
        val updated = cur.copy(
            nombre = nombre,
            telefono = telefono.filter { it.isDigit() }.take(9),
            direccion = direccion
        )
        users[idx] = updated
        return Result.success(updated)
    }

    // Utilidades
    fun getById(id: Int): Credential? = users.firstOrNull { it.idUsuario == id }
    fun all(): List<Credential> = users.toList()
}
