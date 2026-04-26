package com.example.registro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define las operaciones de la base de datos (Data Access Object).
 */
@Dao
interface UnitDao {
    // Inserta una nueva unidad. Lanza una excepción si la placa o número de unidad ya existen.
    @Insert
    suspend fun insertUnit(unit: UnitEntity)

    // Actualiza los datos de una unidad existente
    @Update
    suspend fun updateUnit(unit: UnitEntity)

    // Obtiene todas las unidades registradas, ordenadas por placa.
    @Query("SELECT * FROM refrigerated_units ORDER BY placa ASC")
    fun getAllUnits(): Flow<List<UnitEntity>>

    // Busca unidades que coincidan con la placa (para la Vista 1: Búsqueda)
    @Query("SELECT * FROM refrigerated_units WHERE placa LIKE :searchQuery")
    fun searchByPlaca(searchQuery: String): Flow<List<UnitEntity>>

    // Busca una unidad específica por placa o número de unidad (coincidencia exacta)
    @Query("SELECT * FROM refrigerated_units WHERE placa = :query OR numeroUnidad = :query LIMIT 1")
    suspend fun findUnitByPlacaOrId(query: String): UnitEntity?

    // --- Operaciones para Toma de Temperatura ---
    @Insert
    suspend fun insertTemperature(record: TemperatureEntity)

    @Query("SELECT * FROM temperature_records WHERE fechaHora LIKE :todayDate || '%' ORDER BY id DESC")
    suspend fun getTemperaturesByDate(todayDate: String): List<TemperatureEntity>

    // --- Operaciones de Respaldo (Backup) ---
    @Query("SELECT * FROM refrigerated_units")
    suspend fun getAllUnitsList(): List<UnitEntity>

    @Query("SELECT * FROM temperature_records")
    suspend fun getAllTemperaturesList(): List<TemperatureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnitsList(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemperaturesList(temps: List<TemperatureEntity>)
}
