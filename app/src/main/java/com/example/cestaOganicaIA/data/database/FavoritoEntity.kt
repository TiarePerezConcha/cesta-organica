package com.example.cestaOganicaIA.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: String = "",          
    val nombreProducto: String = ""
)
