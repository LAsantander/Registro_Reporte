
package com.example.registro.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa una unidad refrigerada en la base de datos Room.
 *
 * @property id Identificador único autoincremental para la base de datos.
 * @property placa Identificador legal del vehículo (debe ser único).
 * @property numeroUnidad Identificador interno asignado por la empresa (opcional).
 * @property marca Marca del equipo de refrigeración (ej. Thermo King, Carrier).
 * @property modelo Modelo específico del equipo de refrigeración.
 * @property serie Número de serie único del equipo.
 * @property voltaje Voltaje de operación de la unidad.
 * @property empresa Nombre de la empresa a la que pertenece la unidad.
 */
@Entity(
    tableName = "refrigerated_units",
    indices = [
        Index(value = ["placa"], unique = true),
        Index(value = ["numeroUnidad"], unique = false) // Permitir duplicados entre empresas
    ]
)
data class UnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID autoincremental para la base de datos
    val placa: String,
    val numeroUnidad: String?, // Opcional
    val marca: String = "",
    val modelo: String = "",
    val serie: String = "",
    val voltaje: String = "",
    val empresa: String = ""
)
