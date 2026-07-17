package com.example.registro.model

import com.example.registro.data.TemperatureEntity
import com.example.registro.data.UnitEntity

/**
 * Clase que agrupa toda la información de la base de datos para el respaldo.
 */
data class BackupData(
    val units: List<UnitEntity>,
    val temperatures: List<TemperatureEntity>,
    val workReports: List<com.example.registro.data.WorkReportEntity> = emptyList(),
    val exportDate: String,
    val appVersion: String = "1.1"
)
