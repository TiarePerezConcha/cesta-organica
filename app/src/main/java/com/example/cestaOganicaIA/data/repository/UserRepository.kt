package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

class UserRepository {

    private fun norm(s: String) = s.trim().lowercase()

    /** Convierte una fila JSON de Supabase en un Credential. */
    private fun JSONObject.toCredential(): Credential = Credential(
        uid = optString("uid", ""),
        nombre = optString("nombre", ""),
        correo = optString("correo", ""),
        usuario = optString("usuario", ""),
        telefono = optString("telefono", ""),
        direccion = optString("direccion", ""),
        password = optString("password", ""),
        rol = optString("rol", "user")
    )

    /** Registro de usuario nuevo en la tabla "usuarios" de Supabase. */
    suspend fun register(user: Credential): Result<Credential> = withContext(Dispatchers.IO) {
        try {
            // Verificar si el correo ya existe
            val existing = SupabaseClient.select(
                "usuarios",
                "correo=eq.${SupabaseClient.encode(norm(user.correo))}"
            )
            if (existing.length() > 0) {
                return@withContext Result.failure(Exception("El correo ya está registrado"))
            }

            val nuevoUid = UUID.randomUUID().toString()
            val body = JSONObject().apply {
                put("uid", nuevoUid)
                put("nombre", user.nombre)
                put("correo", norm(user.correo))
                put("usuario", user.usuario)
                put("telefono", user.telefono)
                put("direccion", user.direccion)
                put("password", user.password)
                put("rol", user.rol)
            }

            val created = SupabaseClient.insert("usuarios", body)
            Result.success(created.toCredential())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Login: busca por correo o nombre de usuario, valida contraseña. */
    suspend fun login(usernameOrEmail: String, password: String): Result<Credential> = withContext(Dispatchers.IO) {
        try {
            val campo = if (usernameOrEmail.contains("@")) "correo" else "usuario"
            val valor = norm(usernameOrEmail)

            val resultados = SupabaseClient.select(
                "usuarios",
                "$campo=eq.${SupabaseClient.encode(valor)}"
            )

            if (resultados.length() == 0) {
                return@withContext Result.failure(Exception("Usuario no encontrado"))
            }

            val userJson = resultados.getJSONObject(0)
            val credential = userJson.toCredential()

            if (credential.password != password) {
                return@withContext Result.failure(Exception("Contraseña incorrecta"))
            }

            Result.success(credential)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene todos los usuarios (para el panel de administración). */
    suspend fun getAllUsers(): List<Credential> = withContext(Dispatchers.IO) {
        try {
            val resultados = SupabaseClient.select("usuarios")
            (0 until resultados.length()).map { i ->
                resultados.getJSONObject(i).toCredential()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Actualiza nombre, teléfono y dirección del usuario. */
    suspend fun updateProfile(uid: String, nombre: String, telefono: String, direccion: String): Result<Credential> =
        withContext(Dispatchers.IO) {
            try {
                val updates = JSONObject().apply {
                    put("nombre", nombre)
                    put("telefono", telefono)
                    put("direccion", direccion)
                }
                SupabaseClient.update("usuarios", "uid=eq.${SupabaseClient.encode(uid)}", updates)

                val resultados = SupabaseClient.select("usuarios", "uid=eq.${SupabaseClient.encode(uid)}")
                if (resultados.length() == 0) {
                    return@withContext Result.failure(Exception("Error al recargar datos"))
                }
                Result.success(resultados.getJSONObject(0).toCredential())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /** Obtiene un usuario por su uid. */
    suspend fun getById(uid: String): Credential? = withContext(Dispatchers.IO) {
        try {
            val resultados = SupabaseClient.select("usuarios", "uid=eq.${SupabaseClient.encode(uid)}")
            if (resultados.length() == 0) null else resultados.getJSONObject(0).toCredential()
        } catch (e: Exception) {
            null
        }
    }
}