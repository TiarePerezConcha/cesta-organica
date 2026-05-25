package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PedidoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pedidosCollection = db.collection("pedidos")

    /** Registra un nuevo pedido en Firestore */
    suspend fun confirmarPedido(pedido: PedidoEntity): Result<Unit> {
        return try {
            pedidosCollection.add(pedido).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Obtiene los pedidos de un usuario específico en tiempo real */
    fun pedidosDeUsuario(uid: String): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = pedidosCollection
            .whereEqualTo("usuarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(PedidoEntity::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    /** Solo para admin: todos los pedidos del sistema */
    fun todosPedidos(): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = pedidosCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(PedidoEntity::class.java))
            }
        }
        awaitClose { subscription.remove() }
    }

    /** Actualiza el estado de una orden completa */
    suspend fun actualizarEstadoOrden(ordenId: String, nuevoEstado: String) {
        val snapshot = pedidosCollection.whereEqualTo("ordenId", ordenId).get().await()
        val batch = db.batch()
        for (doc in snapshot.documents) {
            batch.update(doc.reference, "estado", nuevoEstado)
        }
        batch.commit().await()
    }
}
