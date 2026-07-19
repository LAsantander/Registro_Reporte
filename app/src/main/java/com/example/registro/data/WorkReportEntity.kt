package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Entidad para almacenar los reportes de trabajo (mantenimientos, reparaciones, etc.)
 * realizados a las unidades refrigeradas.
 *
 * @property id Identificador único del reporte.
 * @property placa Placa del vehículo intervenido.
 * @property ot Número de Orden de Trabajo asociada al servicio.
 * @property modeloUnidad Marca y modelo del equipo intervenido.
 * @property tipoTrabajo Categoría del trabajo (Preventivo, Correctivo, Eléctrico, etc.).
 * @property descripcion Detalle narrativo de las tareas realizadas.
 * @property tecnico Nombre del técnico responsable de la intervención.
 * @property repuestos Lista de materiales, piezas o repuestos utilizados.
 * @property fechaHora Fecha y hora de creación o actualización del reporte.
 */
@Entity(tableName = "work_reports")
data class WorkReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val ot: String = "", // Orden de Trabajo
    val modeloUnidad: String?, // Antes numeroUnidad, ahora almacenamos el modelo
    val tipoTrabajo: String, // Preventivo, Correctivo, etc.
    val descripcion: String,
    val tecnico: String,
    val repuestos: String,
    val fechaHora: String = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
)
