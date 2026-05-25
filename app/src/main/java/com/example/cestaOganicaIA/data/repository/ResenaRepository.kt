package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Resena
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ResenaRepository {
    // Lista en memoria para guardar las reseñas.
    private val resenas = mutableListOf(
        Resena(
            id = 1,
            nombreProducto = "Manzanas Fuji",
            idUsuario = 2,
            nombreUsuario = "Renatto",
            calificacion = 5,
            comentario = "¡Muy frescas y crujientes, las mejores que he probado!",
            fecha = "25/05/2024"
        ),
        Resena(
            id = 2,
            nombreProducto = "Manzanas Fuji",
            idUsuario = 3,
            nombreUsuario = "John Doe",
            calificacion = 4,
            comentario = "Buenas, aunque un poco pequeñas. En general, recomendables.",
            fecha = "26/05/2024"
        )
    )

    private var proximoId = resenas.size + 1

    // Función para añadir una nueva reseña a la lista
    fun agregarResena(
        nombreProducto: String,
        idUsuario: Int,
        nombreUsuario: String,
        calificacion: Int,
        comentario: String
    ) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        resenas.add(
            Resena(
                id = proximoId++,
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
