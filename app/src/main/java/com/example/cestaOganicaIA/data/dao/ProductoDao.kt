package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("SELECT * FROM productos")
    fun obtenerProductos(): Flow<List<Producto>>
    
    @Query("DELETE FROM productos")
    suspend fun eliminarTodo()

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE nombre = :nombre")
    suspend fun descontarStock(nombre: String, cantidad: Int)
}
