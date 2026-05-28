package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Resena
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ResenaRepository {
    // Lista en memoria para guardar las reseñas.
    private val resenas = mutableListOf(
        Resena(
            id = UUID.randomUUID().toString(),
            nombreProducto = "Manzanas Fuji",
            idUsuario = "user_1",
            nombreUsuario = "Renatto",
            calificacion = 5,
            comentario = "¡Muy frescas y crujientes, las mejores que he probado!",
            fecha = "25/05/2024"
        ),
        Resena(
            id = UUID.randomUUID().toString(),
            nombreProducto = "Manzanas Fuji",
            idUsuario = "user_2",
            nombreUsuario = "John Doe",
            calificacion = 4,
            comentario = "Buenas, aunque un poco pequeñas. En general, recomendables.",
            fecha = "26/05/2024"
        )
    )

    // Función para añadir una nueva reseña a la lista
    fun agregarResena(
        nombreProducto: String,
        idUsuario: String,
        nombreUsuario: String,
        calificacion: Int,
        comentario: String
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        resenas.add(
            Resena(
                id = UUID.randomUUID().toString(),
                nombreProducto = nombreProducto,
                idUsuario = idUsuario,
                nombreUsuario = nombreUsuario,
                calificacion = calificacion,
                comentario = comentario,
                fecha = fechaActual
            )
        )
    }

    // Función para obtener todas las reseñas de un producto específico
    fun obtenerResenasPorProducto(nombreProducto: String): List<Resena> {
        return resenas.filter { it.nombreProducto.equals(nombreProducto, ignoreCase = true) }
    }
}
