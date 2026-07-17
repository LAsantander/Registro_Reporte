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
    entities = [UnitEntity::class, TemperatureEntity::class, WorkReportEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun unitDao(): UnitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de versión 2 a 3: Añade la columna 'unidadTemp'
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN unidadTemp TEXT NOT NULL DEFAULT 'C'")
            }
        }

        // Migración de versión 3 a 4: Añade columnas de alerta para temperaturas
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN isTemp1Alert INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN isTemp2Alert INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migración de versión 4 a 5: Añade la tabla 'work_reports'
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `work_reports` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `placa` TEXT NOT NULL, 
                        `numeroUnidad` TEXT NOT NULL, 
                        `tipoTrabajo` TEXT NOT NULL, 
                        `descripcion` TEXT NOT NULL, 
                        `tecnico` TEXT NOT NULL, 
                        `repuestos` TEXT NOT NULL, 
                        `fechaHora` TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registro_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5) // Aplicamos las migraciones manuales seguras
                // Ya NO usamos fallbackToDestructiveMigration() para proteger los datos
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
