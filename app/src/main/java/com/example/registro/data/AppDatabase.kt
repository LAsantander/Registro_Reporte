package com.example.registro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Clase principal de la base de datos Room de la aplicación.
 * Define las tablas (entidades), la versión del esquema y gestiona las migraciones.
 */
@Database(
    entities = [UnitEntity::class, TemperatureEntity::class, WorkReportEntity::class],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso a las operaciones definidas en el DAO.
     */
    abstract fun unitDao(): UnitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migración de versión 2 a 3: Incorpora la columna 'unidadTemp' para soportar Fahrenheit.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN unidadTemp TEXT NOT NULL DEFAULT 'C'")
            }
        }

        /**
         * Migración de versión 3 a 4: Incorpora banderas de alerta para identificar tomas críticas.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN isTemp1Alert INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE temperature_records ADD COLUMN isTemp2Alert INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Migración de versión 4 a 5: Creación inicial de la tabla de reportes de trabajo.
         */
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

        /**
         * Singleton para obtener la instancia de la base de datos.
         * Gestiona la creación del archivo de base de datos y la aplicación de migraciones estructurales.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "registro_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5) // Migraciones manuales seguras
                .fallbackToDestructiveMigration(dropAllTables = true) // Reparación automática ante cambios de esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
