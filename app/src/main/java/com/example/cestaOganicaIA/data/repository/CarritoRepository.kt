package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CarritoRepository {
    private val db = FirebaseFirestore.getInstance()
    private fun getCartCollection(uid: String) = db.collection("carritos").document(uid).collection("items")

    fun itemsDeUsuario(uid: String): Flow<List<CarritoItemEntity>> = callbackFlow {
        val subscription = getCartCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(CarritoItemEntity::class.java))
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun agregarOActualizar(uid: String, nombreProducto: String, precio: Int, imagenResId: Int, cantidad: Int = 1) {
        val collection = getCartCollection(uid)
        val snapshot = collection.whereEqualTo("nombreProducto", nombreProducto).get().await()
        
        if (!snapshot.isEmpty) {
            val doc = snapshot.documents.first()
            val currentQty = doc.getLong("cantidad") ?: 0
            doc.reference.update("cantidad", currentQty + cantidad).await()
        } else {
            val newItem = CarritoItemEntity(
                usuarioId = uid,
                nombreProducto = nombreProducto,
                precioUnitario = precio,
                cantidad = cantidad,
                imagenResId = imagenResId
            )
            collection.add(newItem).await()
        }
    }

    suspend fun cambiarCantidad(uid: String, itemId: String, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            getCartCollection(uid).document(itemId).delete().await()
        } else {
            getCartCollection(uid).document(itemId).update("cantidad", nuevaCantidad).await()
        }
    }

    suspend fun eliminarItem(uid: String, itemId: String) {
        getCartCollection(uid).document(itemId).delete().await()
    }

    suspend fun vaciarCarrito(uid: String) {
        val batch = db.batch()
        val snapshot = getCartCollection(uid).get().await()
        for (doc in snapshot.documents) {
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }
}
