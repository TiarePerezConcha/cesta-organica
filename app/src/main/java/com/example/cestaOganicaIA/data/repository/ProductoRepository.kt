package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productosCollection = db.collection("productos")

    /** Obtiene la lista de productos en tiempo real desde Firestore */
    fun obtenerProductos(): Flow<List<Producto>> = callbackFlow {
        val subscription = productosCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObjects(Producto::class.java)
                trySend(list)
            }
        }
        awaitClose { subscription.remove() }
    }

    /** Agrega o actualiza un producto (Útil para inicializar la DB) */
    suspend fun guardarProducto(producto: Producto): Result<Unit> {
        return try {
            if (producto.id.isEmpty()) {
                productosCollection.add(producto).await()
            } else {
                productosCollection.document(producto.id).set(producto).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
