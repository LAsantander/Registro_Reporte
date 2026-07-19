package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Entidad para almacenar los registros históricos de tomas de temperatura.
 * Cada registro es independiente para mantener una trazabilidad plana por fecha.
 *
 * @property id Identificador único del registro.
 * @property placa Placa del vehículo al que se le tomó la temperatura.
 * @property numeroUnidad Número interno de la unidad en el momento de la toma.
 * @property temp1 Valor numérico de la primera toma de temperatura.
 * @property isTemp1Alert Indica si la temperatura 1 fue marcada como crítica por el técnico.
 * @property temp2 Valor numérico de la segunda toma de temperatura.
 * @property isTemp2Alert Indica si la temperatura 2 fue marcada como crítica por el técnico.
 * @property unidadTemp Escala de temperatura utilizada (C para Celsius, F para Fahrenheit).
 * @property comentarios Observaciones o notas adicionales capturadas durante la toma.
 * @property fechaHora Fecha y hora exacta del registro (generada automáticamente).
 */
@Entity(tableName = "temperature_records")
data class TemperatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val numeroUnidad: String?,
    val temp1: String,
    val isTemp1Alert: Boolean = false, // Marca si la Temp 1 es crítica (rojo)
    val temp2: String,
    val isTemp2Alert: Boolean = false, // Marca si la Temp 2 es crítica (rojo)
    val unidadTemp: String = "C",
    val comentarios: String,
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
)
