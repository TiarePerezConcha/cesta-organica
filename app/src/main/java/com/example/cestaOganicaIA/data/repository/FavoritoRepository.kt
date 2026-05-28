package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.FavoritoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("favoritos")

    /** Obtiene los favoritos del usuario desde Firebase Firestore */
    fun favoritosDeUsuario(uid: String): Flow<List<FavoritoEntity>> = callbackFlow {
        if (uid.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }
        val subscription = collection
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val favs = snapshot.toObjects(FavoritoEntity::class.java)
                    trySend(favs)
                }
            }
        awaitClose { subscription.remove() }
    }

    /** Agrega o quita un producto de favoritos en Firestore */
    suspend fun toggleFavorito(uid: String, nombreProducto: String) {
        if (uid.isEmpty()) return
        
        val snapshot = collection
            .whereEqualTo("usuarioId", uid)
            .whereEqualTo("nombreProducto", nombreProducto)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            // Ya existe, lo quitamos
            for (doc in snapshot.documents) {
                collection.document(doc.id).delete().await()
            }
        } else {
            // No existe, lo agregamos
            val docRef = collection.document()
            val newFav = FavoritoEntity(usuarioId = uid, nombreProducto = nombreProducto)
            docRef.set(newFav).await()
        }
    }

    /** Verifica si un producto es favorito (vía consulta simple) */
    suspend fun esFavorito(uid: String, nombreProducto: String): Boolean {
        if (uid.isEmpty()) return false
        val snapshot = collection
            .whereEqualTo("usuarioId", uid)
            .whereEqualTo("nombreProducto", nombreProducto)
            .get()
            .await()
        return !snapshot.isEmpty
    }
}
