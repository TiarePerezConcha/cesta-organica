package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.dao.CarritoDao
import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val carritoDao: CarritoDao) {

    fun itemsDeUsuario(uid: String): Flow<List<CarritoItemEntity>> = carritoDao.obtenerPorUsuario(uid)

    suspend fun agregarOActualizar(uid: String, nombre: String, precio: Double, imagenResId: Int, cantidad: Int) {
        val existente = carritoDao.buscarItem(uid, nombre)
        if (existente != null) {
            carritoDao.actualizar(existente.copy(cantidad = existente.cantidad + cantidad))
        } else {
            carritoDao.insertar(
                CarritoItemEntity(
                    usuarioId = uid,
                    nombreProducto = nombre,
                    precioUnitario = precio,
                    cantidad = cantidad,
                    imagenResId = imagenResId
                )
            )
        }
    }

    suspend fun cambiarCantidad(item: CarritoItemEntity, nuevaCantidad: Int) {
        if (nuevaCantidad > 0) {
            carritoDao.actualizar(item.copy(cantidad = nuevaCantidad))
        } else {
            carritoDao.eliminar(item)
        }
    }

    suspend fun eliminarItem(item: CarritoItemEntity) {
        carritoDao.eliminar(item)
    }

    suspend fun vaciarCarrito(uid: String) {
        carritoDao.vaciar(uid)
    }
}
