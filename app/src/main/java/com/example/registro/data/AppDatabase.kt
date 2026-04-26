package com.example.registro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Clase principal de la base de datos Room.
 */
@Database(
    entities = [UnitEntity::class, TemperatureEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun unitDao(): UnitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de versión 2 a 3: Añade la columna 'unidadTemp' sin borrar los datos
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN unidadTemp TEXT NOT NULL DEFAULT 'C'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registro_database"
                )
                .addMigrations(MIGRATION_2_3) // Aplicamos la migración manual segura
                // Ya NO usamos fallbackToDestructiveMigration() para proteger los datos
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
