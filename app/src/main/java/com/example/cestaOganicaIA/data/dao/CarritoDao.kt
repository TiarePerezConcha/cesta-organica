package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito_items WHERE usuarioId = :uid")
    fun obtenerPorUsuario(uid: String): Flow<List<CarritoItemEntity>>

    @Query("SELECT * FROM carrito_items WHERE usuarioId = :uid AND nombreProducto = :nombre LIMIT 1")
    suspend fun buscarItem(uid: String, nombre: String): CarritoItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: CarritoItemEntity)

    @Update
    suspend fun actualizar(item: CarritoItemEntity)

    @Delete
    suspend fun eliminar(item: CarritoItemEntity)

    @Query("DELETE FROM carrito_items WHERE usuarioId = :uid")
    suspend fun vaciar(uid: String)
}
