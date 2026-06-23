package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class PedidoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("pedidos")

    /**
     * Formato en que se guarda fechaPedido: "dd/MM/yyyy HH:mm"
     * Se usa para ordenar por fecha real, no alfabéticamente.
     */
    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private fun parsearFecha(fecha: String): Long {
        return try {
            formatoFecha.parse(fecha)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Obtiene los pedidos del usuario en tiempo real desde Firestore.
     * El ordenamiento se hace en memoria (Kotlin) por fecha real,
     * para evitar requerir un índice compuesto en Firestore.
     */
    fun obtenerPorUsuario(uid: String): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("usuarioId", uid)
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
    }.map { lista ->
        lista.sortedByDescending { parsearFecha(it.fechaPedido) }
    }

    /**
     * Guarda un pedido en Firestore.
     */
    suspend fun confirmarPedido(pedido: PedidoEntity) {
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
     * Mismo criterio: ordenamos por fecha real en memoria.
     */
    fun obtenerTodos(): Flow<List<PedidoEntity>> = callbackFlow {
        val subscription = collection
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
    }.map { lista ->
        lista.sortedByDescending { parsearFecha(it.fechaPedido) }
    }
}