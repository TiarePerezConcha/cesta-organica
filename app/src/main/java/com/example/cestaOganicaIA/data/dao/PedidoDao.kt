package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.database.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos ORDER BY idLocal DESC")
    fun obtenerTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE usuarioId = :uid ORDER BY idLocal DESC")
    fun obtenerPorUsuario(uid: String): Flow<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pedido: PedidoEntity)

    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE ordenId = :ordenId")
    suspend fun actualizarEstadoOrden(ordenId: String, nuevoEstado: String)
}
