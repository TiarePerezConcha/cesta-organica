package com.example.cestaOganicaIA.data.dao

import androidx.room.*
import com.example.cestaOganicaIA.data.model.Credential

@Dao
interface UserDao {
    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Credential>

    @Query("SELECT * FROM usuarios WHERE uid = :uid")
    suspend fun getById(uid: String): Credential?

    @Query("SELECT * FROM usuarios WHERE usuario = :query OR correo = :query")
    suspend fun getByUsernameOrEmail(query: String): Credential?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Credential)

    @Update
    suspend fun update(user: Credential)

    @Delete
    suspend fun delete(user: Credential)
}
