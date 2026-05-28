package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PedidoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("pedidos")

    /**
     * Obtiene los pedidos del usuario en tiempo real desde Firestore.
     */
    fun obtenerPorUsuario(uid: String): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("usuarioId", uid)
            .orderBy("fechaPedido", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(PedidoEntity::class.java)
                    trySend(pedidos)
                }
            }
        awaitClose { subscription.remove() }
    }

    /**
     * Guarda un pedido en Firestore.
     */
    suspend fun confirmarPedido(pedido: PedidoEntity) {
        // Generamos un ID de documento si no tiene uno
        val docRef = if (pedido.id.isEmpty()) {
            collection.document()
        } else {
            collection.document(pedido.id)
        }
        
        val pedidoFinal = pedido.copy(id = docRef.id)
        docRef.set(pedidoFinal).await()
    }

    /**
     * Obtiene todos los pedidos (para el administrador).
     */
    fun obtenerTodos(): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = collection
            .orderBy("fechaPedido", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val pedidos = snapshot.toObjects(PedidoEntity::class.java)
                    trySend(pedidos)
                }
            }
        awaitClose { subscription.remove() }
    }
}
