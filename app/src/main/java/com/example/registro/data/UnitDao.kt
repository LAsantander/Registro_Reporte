package com.example.registro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define las operaciones de la base de datos (Data Access Object).
 */
@Dao
interface UnitDao {
    // Inserta una nueva unidad. Si la placa ya existe, la reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitEntity)

    // Obtiene todas las unidades registradas, ordenadas por placa.
    @Query("SELECT * FROM refrigerated_units ORDER BY placa ASC")
    fun getAllUnits(): Flow<List<UnitEntity>>

    // Busca unidades que coincidan con la placa (para la Vista 1: Búsqueda)
    @Query("SELECT * FROM refrigerated_units WHERE placa LIKE :searchQuery")
    fun searchByPlaca(searchQuery: String): Flow<List<UnitEntity>>
}
