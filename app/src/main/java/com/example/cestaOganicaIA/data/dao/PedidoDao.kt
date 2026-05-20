package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.database.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos")
    fun obtenerTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE usuarioId = :uid")
    fun obtenerPorUsuario(uid: Int): Flow<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pedido: PedidoEntity)

    @Update
    suspend fun actualizar(pedido: PedidoEntity)

    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE ordenId = :ordenId")
    suspend fun actualizarEstado(ordenId: Int, nuevoEstado: String)
}
