package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ResenaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("resenas")

    /** Obtiene en tiempo real las reseñas de un producto, ordenadas por fecha. */
    fun obtenerResenasPorProducto(nombreProducto: String): Flow<List<Resena>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("nombreProducto", nombreProducto)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val resenas = snapshot.toObjects(Resena::class.java)
                    trySend(resenas)
                }
            }
        awaitClose { subscription.remove() }
    }

    /** Guarda una nueva reseña en Firestore. */
    suspend fun agregarResena(
        nombreProducto: String,
        idUsuario: String,
        nombreUsuario: String,
        calificacion: Int,
        comentario: String
    ) {
        val docRef = collection.document()
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val resena = Resena(
            id = docRef.id,
            nombreProducto = nombreProducto,
            idUsuario = idUsuario,
            nombreUsuario = nombreUsuario,
            calificacion = calificacion,
            comentario = comentario,
            fecha = fechaActual
        )
        docRef.set(resena).await()
    }
}