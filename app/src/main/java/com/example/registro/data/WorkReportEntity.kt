package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Entidad para almacenar los reportes de trabajo realizados a las unidades.
 */
@Entity(tableName = "work_reports")
data class WorkReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val numeroUnidad: String,
    val tipoTrabajo: String, // Preventivo, Correctivo, etc.
    val descripcion: String,
    val tecnico: String,
    val repuestos: String,
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
)
