package com.example.cestaOganicaIA.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cestaOganicaIA.data.dao.*
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.model.Producto

@Database(
    entities = [
        Producto::class,
        CarritoItemEntity::class,
        FavoritoEntity::class,
        PedidoEntity::class,
        Credential::class
    ],
    version = 7, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cesta_organica_v7_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
