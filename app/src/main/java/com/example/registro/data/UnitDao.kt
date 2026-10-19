package com.example.registro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define las operaciones de acceso a datos (Data Access Object).
 * Gestiona todas las consultas SQL necesarias para la persistencia local con Room.
 */
@Dao
interface UnitDao {
    /**
     * Inserta una nueva unidad técnica.
     * Lanza una excepción si la placa ya existe debido a la restricción UNIQUE.
     */
    @Insert
    suspend fun insertUnit(unit: UnitEntity)

    /**
     * Actualiza los datos de una unidad técnica existente.
     */
    @Update
    suspend fun updateUnit(unit: UnitEntity)

    /**
     * Obtiene todas las unidades registradas, ordenadas alfabéticamente por placa.
     */
    @Query("SELECT * FROM refrigerated_units ORDER BY placa ASC")
    fun getAllUnits(): Flow<List<UnitEntity>>

    /**
     * Realiza una búsqueda parcial de unidades por placa (útil para autocompletado).
     */
    @Query("SELECT * FROM refrigerated_units WHERE placa LIKE :searchQuery")
    fun searchByPlaca(searchQuery: String): Flow<List<UnitEntity>>

    /**
     * Busca una unidad específica por su placa o su número interno (coincidencia exacta).
     */
    @Query("SELECT * FROM refrigerated_units WHERE placa = :query OR numeroUnidad = :query LIMIT 1")
    suspend fun findUnitByPlacaOrId(query: String): UnitEntity?

    // --- Operaciones para Toma de Temperatura ---

    /**
     * Registra una nueva toma de temperatura.
     */
    @Insert
    suspend fun insertTemperature(record: TemperatureEntity)

    /**
     * Recupera todos los registros de temperatura de una fecha específica.
     */
    @Query("SELECT * FROM temperature_records WHERE fechaHora LIKE :todayDate || '%' ORDER BY id DESC")
    suspend fun getTemperaturesByDate(todayDate: String): List<TemperatureEntity>

    /**
     * Obtiene los últimos 5 registros de temperatura para visualización rápida.
     */
    @Query("SELECT * FROM temperature_records WHERE fechaHora LIKE :todayDate || '%' ORDER BY id DESC LIMIT 5")
    fun getRecentTemperatures(todayDate: String): Flow<List<TemperatureEntity>>

    /**
     * Elimina un registro de temperatura por su identificador.
     */
    @Query("DELETE FROM temperature_records WHERE id = :id")
    suspend fun deleteTemperatureById(id: Int)

    // --- Operaciones para Reportes de Trabajo ---

    /**
     * Registra un nuevo reporte de servicio.
     */
    @Insert
    suspend fun insertWorkReport(report: WorkReportEntity)

    /**
     * Actualiza un reporte de servicio existente.
     */
    @Update
    suspend fun updateWorkReport(report: WorkReportEntity)

    /**
     * Elimina un reporte de servicio por su identificador.
     */
    @Query("DELETE FROM work_reports WHERE id = :id")
    suspend fun deleteWorkReportById(id: Int)

    /**
     * Obtiene el historial completo de trabajos de una unidad específica.
     */
    @Query("SELECT * FROM work_reports WHERE placa = :placa ORDER BY id DESC")
    suspend fun getWorkReportsByUnit(placa: String): List<WorkReportEntity>

    /**
     * Obtiene los últimos 10 reportes de trabajo globales para visualización rápida.
     */
    @Query("SELECT * FROM work_reports ORDER BY id DESC LIMIT 10")
    fun getRecentWorkReports(): Flow<List<WorkReportEntity>>

    // --- Operaciones de Respaldo (Backup) ---

    /**
     * Obtiene la lista plana de todas las unidades para exportación.
     */
    @Query("SELECT * FROM refrigerated_units")
    suspend fun getAllUnitsList(): List<UnitEntity>

    /**
     * Obtiene la lista plana de todas las tomas de temperatura para exportación.
     */
    @Query("SELECT * FROM temperature_records")
    suspend fun getAllTemperaturesList(): List<TemperatureEntity>

    /**
     * Obtiene la lista plana de todos los reportes de trabajo para exportación.
     */
    @Query("SELECT * FROM work_reports")
    suspend fun getAllWorkReportsList(): List<WorkReportEntity>

    /**
     * Inserta masivamente una lista de unidades (usado en Importar).
     * Sobrescribe registros si la placa ya existe.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnitsList(units: List<UnitEntity>)

    /**
     * Inserta masivamente una lista de temperaturas (usado en Importar).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperaturesList(temps: List<TemperatureEntity>)

    /**
     * Inserta masivamente una lista de reportes de trabajo (usado en Importar).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkReportsList(reports: List<WorkReportEntity>)

    // --- Operaciones para Alarmas ---

    /**
     * Registra o reemplaza una alarma.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    /**
     * Inserta masivamente una lista de alarmas (usado en Importar).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarmsList(alarms: List<AlarmEntity>)

    /**
     * Obtiene los modelos de unidades distintos registrados en la tabla de alarmas.
     */
    @Query("SELECT DISTINCT modelo FROM alarms ORDER BY modelo ASC")
    fun getAlarmModels(): Flow<List<String>>

    /**
     * Busca una alarma por su modelo de unidad y su código/número exacto.
     */
    @Query("SELECT * FROM alarms WHERE UPPER(modelo) = UPPER(:modelo) AND UPPER(codigoAlarma) = UPPER(:codigo) LIMIT 1")
    suspend fun findAlarmByModelAndCode(modelo: String, codigo: String): AlarmEntity?

    /**
     * Obtiene todas las alarmas registradas para un modelo específico.
     */
    @Query("SELECT * FROM alarms WHERE UPPER(modelo) = UPPER(:modelo) ORDER BY codigoAlarma ASC")
    fun getAlarmsByModel(modelo: String): Flow<List<AlarmEntity>>

    /**
     * Obtiene todas las alarmas de la base de datos agrupadas por modelo y código.
     */
    @Query("SELECT * FROM alarms ORDER BY modelo ASC, codigoAlarma ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    /**
     * Obtiene la lista plana de todas las alarmas para exportación/respaldo.
     */
    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarmsList(): List<AlarmEntity>

    /**
     * Elimina una alarma por su identificador.
     */
    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Int)
}
