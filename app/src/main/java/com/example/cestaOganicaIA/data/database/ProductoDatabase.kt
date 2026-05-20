package com.example.cestaOganicaIA.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cestaOganicaIA.data.dao.ProductoDao
import com.example.cestaOganicaIA.data.model.Producto

@Database(
    entities = [Producto::class],
    version = 1,
    exportSchema = false // evita warnings al compilar
)
abstract class ProductoDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: ProductoDatabase? = null

        fun getDatabase(context: Context): ProductoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductoDatabase::class.java,
                    "producto_database"
                )
                    // 🔹 Evita que la app se cierre si cambias el modelo de datos
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
