package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.dao.PedidoDao
import com.example.cestaOganicaIA.data.database.PedidoEntity
import kotlinx.coroutines.flow.Flow

class PedidoRepository(private val pedidoDao: PedidoDao) {

    fun obtenerTodos(): Flow<List<PedidoEntity>> = pedidoDao.obtenerTodos()

    fun obtenerPorUsuario(uid: Int): Flow<List<PedidoEntity>> = pedidoDao.obtenerPorUsuario(uid)

    suspend fun confirmarPedido(pedido: PedidoEntity) {
        pedidoDao.insertar(pedido)
    }

    suspend fun actualizarEstado(ordenId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(ordenId, nuevoEstado)
    }
}
