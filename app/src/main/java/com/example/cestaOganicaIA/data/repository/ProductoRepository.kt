package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.dao.ProductoDao
import com.example.cestaOganicaIA.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    suspend fun insertarProducto(producto: Producto) {
        productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: Producto) {
        productoDao.actualizarProducto(producto)
    }

    suspend fun eliminarProducto(producto: Producto) {
        productoDao.eliminarProducto(producto)
    }

    suspend fun eliminarTodo() {
        productoDao.eliminarTodo()
    }

    fun obtenerProductos(): Flow<List<Producto>> {
        return productoDao.obtenerProductos()
    }

    suspend fun descontarStock(nombre: String, cantidad: Int) {
        productoDao.descontarStock(nombre, cantidad)
    }
}
