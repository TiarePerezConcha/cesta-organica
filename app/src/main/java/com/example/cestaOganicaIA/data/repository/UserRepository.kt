package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("usuarios")

    private fun norm(s: String) = s.trim().lowercase()

    // Registro en Firebase
    suspend fun register(user: Credential): Result<Credential> {
        return try {
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(user.correo, user.password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("No se pudo obtener el UID"))

            // 2. Guardar datos adicionales en Firestore
            val userWithUid = user.copy(uid = uid, password = "") // No guardamos la clave en Firestore por seguridad
            collection.document(uid).set(userWithUid).await()

            Result.success(userWithUid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login en Firebase
    suspend fun login(usernameOrEmail: String, password: String): Result<Credential> {
        return try {
            // Si es un correo, intentamos login directo
            val email = if (usernameOrEmail.contains("@")) {
                usernameOrEmail
            } else {
                // Si es un nombre de usuario, debemos buscar el correo en Firestore primero
                val snapshot = collection.whereEqualTo("usuario", usernameOrEmail).get().await()
                if (snapshot.isEmpty) return Result.failure(Exception("Usuario no encontrado"))
                snapshot.documents.first().getString("correo") ?: return Result.failure(Exception("Correo no encontrado"))
            }

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("Login fallido"))

            // Obtener datos del perfil desde Firestore
            val doc = collection.document(uid).get().await()
            val userData = doc.toObject(Credential::class.java) ?: return Result.failure(Exception("Datos de usuario no encontrados"))
            
            Result.success(userData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todos los usuarios de Firestore
    suspend fun getAllUsers(): List<Credential> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Credential::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Actualizar perfil en Firestore
    suspend fun updateProfile(uid: String, nombre: String, telefono: String, direccion: String): Result<Credential> {
        return try {
            val updates = mapOf(
                "nombre" to nombre,
                "telefono" to telefono,
                "direccion" to direccion
            )
            collection.document(uid).update(updates).await()
            
            val doc = collection.document(uid).get().await()
            val updated = doc.toObject(Credential::class.java) ?: return Result.failure(Exception("Error al recargar datos"))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getById(uid: String): Credential? {
        return try {
            val doc = collection.document(uid).get().await()
            doc.toObject(Credential::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
