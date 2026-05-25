package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object ResenaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val resenasCollection = db.collection("resenas")

    suspend fun agregarResena(nombreProducto: String, idUsuario: String, nombreUsuario: String, calificacion: Int, comentario: String): Result<Unit> {
        return try {
            val resena = Resena(
                id = "",
                nombreProducto = nombreProducto,
                idUsuario = idUsuario,
                nombreUsuario = nombreUsuario,
                calificacion = calificacion,
                comentario = comentario,
                fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            )
            resenasCollection.add(resena).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerResenasPorProducto(nombreProducto: String): List<Resena> {
        return try {
            resenasCollection
                .whereEqualTo("nombreProducto", nombreProducto)
                .get()
                .await()
                .toObjects(Resena::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
