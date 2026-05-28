package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.database.FavoritoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregar(fav: FavoritoEntity)

    @Delete
    suspend fun eliminar(fav: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE usuarioId = :uid AND nombreProducto = :nombre")
    suspend fun eliminarPorNombre(uid: String, nombre: String)

    @Query("SELECT * FROM favoritos WHERE usuarioId = :uid")
    fun obtenerPorUsuario(uid: String): Flow<List<FavoritoEntity>>

    @Query("SELECT COUNT(*) FROM favoritos WHERE usuarioId = :uid AND nombreProducto = :nombre")
    suspend fun esFavorito(uid: String, nombre: String): Int
}
