package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Entidad para almacenar los registros de toma de temperatura.
 * Es independiente de la tabla de unidades para mantener un historial histórico plano.
 */
@Entity(tableName = "temperature_records")
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val numeroUnidad: String,
    val temp1: String,
    val isTemp1Alert: Boolean = false, // Marca si la Temp 1 es crítica (rojo)
    val temp2: String,
    val isTemp2Alert: Boolean = false, // Marca si la Temp 2 es crítica (rojo)
    val unidadTemp: String = "C",
    val comentarios: String,
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
)
