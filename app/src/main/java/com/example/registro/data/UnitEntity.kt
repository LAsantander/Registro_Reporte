package com.example.registro.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa una unidad refrigerada en la base de datos Room.
 * Se han añadido índices únicos para 'placa' y 'numeroUnidad' para asegurar que
 * no se dupliquen y que cada unidad esté correctamente identificada.
 */
@Entity(
    tableName = "refrigerated_units",
    indices = [
        Index(value = ["placa"], unique = true),
        Index(value = ["numeroUnidad"], unique = true)
    ]
)
data class UnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // ID autoincremental para la base de datos
    val placa: String,
    val numeroUnidad: String, // Cambiado de 'empresa' a 'numeroUnidad'
    val marca: String,
    val modelo: String,
    val serie: String
)
