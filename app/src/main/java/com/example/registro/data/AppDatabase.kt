package com.example.registro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase principal de la base de datos Room.
 * Define la versión y las entidades que contiene.
 */
@Database(entities = [UnitEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Define el acceso al DAO
    abstract fun unitDao(): UnitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (Singleton).
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registro_database" // Nombre del archivo de la DB
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
