package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de usuarios conectado a Firebase Firestore.
 */
object UserRepository {
    private val db = FirebaseFirestore.getInstance()
    // Asegúrate de que la colección se llame "usuarios" o "users" según tu consola Firebase
    private val usersCollection = db.collection("usuarios")

    /** Guarda o actualiza el perfil de un usuario en Firestore */
    suspend fun saveProfile(user: Credential): Result<Unit> {
        return try {
            // Usamos .uid que es el campo correcto según tu modelo
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene el perfil de un usuario por su UID */
    suspend fun getProfile(uid: String): Credential? {
        return try {
            val snapshot = usersCollection.document(uid).get().await()
            snapshot.toObject(Credential::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /** * Actualiza el perfil y devuelve el objeto Credential actualizado.
     * CORRECCIÓN: Se eliminó la versión duplicada que devolvía Unit para evitar conflictos.
     */
    suspend fun updateProfile(
        uid: String,
        nombre: String,
        telefono: String,
        direccion: String
    ): Result<Credential> {
        return try {
            val updates = mapOf(
                "nombre" to nombre,
                "telefono" to telefono,
                "direccion" to direccion
            )
            // 1. Actualizamos en Firestore
            usersCollection.document(uid).update(updates).await()

            // 2. Obtenemos el documento completo para devolver el objeto actualizado
            val snapshot = usersCollection.document(uid).get().await()
            val userUpdated = snapshot.toObject(Credential::class.java)

            if (userUpdated != null) {
                Result.success(userUpdated)
            } else {
                Result.failure(Exception("No se pudo reconstruir el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Elimina el documento del usuario */
    suspend fun deleteProfile(uid: String): Result<Unit> {
        return try {
            usersCollection.document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene todos los usuarios (Requerido para el Panel de Admin) */
    suspend fun getAllUsers(): List<Credential> {
        return try {
            usersCollection.get().await().toObjects(Credential::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}