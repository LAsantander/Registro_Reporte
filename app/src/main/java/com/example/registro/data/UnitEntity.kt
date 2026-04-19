package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una unidad refrigerada en la base de datos Room.
 * Cada campo corresponde a una columna en la tabla "refrigerated_units".
 */
@Entity(tableName = "refrigerated_units")
data class UnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID autoincremental para la base de datos
    val placa: String,
    val empresa: String,
    val marca: String,
    val modelo: String,
    val serie: String
)
