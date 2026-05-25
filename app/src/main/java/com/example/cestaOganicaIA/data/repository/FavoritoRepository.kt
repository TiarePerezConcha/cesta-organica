package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.FavoritoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoritoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritosCollection = db.collection("favoritos")

    /** Obtiene los favoritos del usuario en tiempo real */
    fun favoritosDeUsuario(uid: String): Flow<List<FavoritoEntity>> = callbackFlow {
        val subscription = favoritosCollection
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(FavoritoEntity::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    /** Agrega o quita un producto de favoritos */
    suspend fun toggleFavorito(uid: String, nombreProducto: String) {
        val docId = "${uid}_${nombreProducto}"
        val docRef = favoritosCollection.document(docId)
        val snapshot = docRef.get().await()

        if (snapshot.exists()) {
            docRef.delete().await()
        } else {
            docRef.set(FavoritoEntity(uid, nombreProducto)).await()
        }
    }

    /** Verifica si un producto es favorito (vía get rápido) */
    suspend fun esFavorito(uid: String, nombreProducto: String): Boolean {
        val docId = "${uid}_${nombreProducto}"
        return try {
            favoritosCollection.document(docId).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
}
