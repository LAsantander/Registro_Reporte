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
    entities = [UnitEntity::class, TemperatureEntity::class, WorkReportEntity::class, AlarmEntity::class],
    version = 8,
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
         * Migración de versión 7 a 8: Creación inicial de la tabla de alarmas.
         */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `alarms` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `modelo` TEXT NOT NULL, 
                        `codigoAlarma` TEXT NOT NULL, 
                        `descripcion` TEXT NOT NULL, 
                        `solucion` TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                insertDefaultAlarms(db)
            }
        }

        /**
         * Inserta las alarmas iniciales más comunes para Thermo King y Carrier.
         */
        private fun insertDefaultAlarms(db: SupportSQLiteDatabase) {
            val defaultAlarms = listOf(
                // Thermo King
                Triple("Thermo King", "02", Pair("Comprobar el sensor del serpentín del evaporador ", " Este sensor no es ajustado y tampoco es necesario calibrarlo, El sensor esta situado en la placa del cabezal del serpentín del evaporador " )),
                Triple("Thermo King", "03", Pair("Verificar sensor de temperatura de aire de retorno", "El sensor se encuentra en la corriente del aire de retorno. Se trata de un sensor ajustado.")),
                Triple("Thermo King", "04", Pair("Verificar sensor de temperatura de aire de descarga", "El sensor se encuentra en la corriente del aire de descarga. Se trata de un sensor ajustado.")),
                Triple("Thermo King", "05", Pair("Comprobar el sensor de temperatura ambiente", "Este sensor no es ajustado y tampoco es necesario calibrarlo, El sensor esta situado en la corriente de aire del condensador, detrás de la rejilla del condensador.")),
                Triple("Thermo King", "06" +
                        "" +
                        "", Pair("Sensor de temperatura de aire de retorno en falla", "Reemplazar o calibrar el sensor de aire de retorno.")),
                Triple("Thermo King", "17", Pair("El motor diésel no arrancó", "Comprobar combustible, batería, bomba y solenoide de pare.")),
                Triple("Thermo King", "18", Pair("Alta temperatura de refrigerante del motor (Apagado)", "Revisar refrigerante, bomba de agua y faja de ventilación.")),
                Triple("Thermo King", "20", Pair("El motor diésel no arrancó (Parada)", "Inspeccionar sistema de combustible, arranque y batería.")),
                Triple("Thermo King", "26", Pair("Comprobar capacidad de refrigeración", "Verificar presiones de refrigerante y congelamiento de evaporador.")),
                Triple("Thermo King", "35", Pair("Comprobar circuito de relé de marcha", "Verificar relés de la caja de control y fusibles.")),
                Triple("Thermo King", "63", Pair("Parada por alta presión de condensación", "Limpiar serpentín condensador y comprobar ventilador.")),
                Triple("Thermo King", "84", Pair("Nivel bajo de aceite de motor", "Comprobar nivel de aceite del motor diésel y buscar fugas.")),

                // Carrier
                Triple("Carrier", "A01", Pair("Baja presión de aceite del motor", "Revisar nivel de aceite, filtro y sensor de presión de aceite.")),
                Triple("Carrier", "A02", Pair("Alta temperatura de refrigerante de motor", "Inspeccionar radiador, faja y líquido refrigerante.")),
                Triple("Carrier", "A03", Pair("Nivel bajo de refrigerante", "Inspeccionar radiador y mangueras por posibles fugas.")),
                Triple("Carrier", "A10", Pair("Alta presión de condensación / Descarga", "Limpiar serpentín de condensador y revisar motor del condensador.")),
                Triple("Carrier", "A11", Pair("Baja presión de succión", "Revisar carga de refrigerante, filtro deshidratador y VXT.")),
                Triple("Carrier", "A28", Pair("Falla en alternador o sistema de carga", "Comprobar correa de alternador, regulador y bornes de batería."))
            )

            for ((modelo, codigo, data) in defaultAlarms) {
                db.execSQL(
                    "INSERT OR IGNORE INTO `alarms` (`modelo`, `codigoAlarma`, `descripcion`, `solucion`) VALUES (?, ?, ?, ?)",
                    arrayOf(modelo, codigo, data.first, data.second)
                )
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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_7_8) // Migraciones manuales seguras
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        insertDefaultAlarms(db)
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true) // Reparación automática ante cambios de esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
