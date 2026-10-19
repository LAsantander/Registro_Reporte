package com.example.registro.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una alarma de una unidad de refrigeración/transporte.
 *
 * @property id Identificador único autoincremental de la alarma.
 * @property modelo Modelo o marca de la unidad (ej. "Thermo King", "Carrier Vector", "Carrier Supra", "T-600", etc.).
 * @property codigoAlarma Código o número de la alarma (ej. "10", "12", "20", "63", "A01", "P01", etc.).
 * @property descripcion Descripción de la falla o condición reportada por la alarma.
 * @property solucion Acción recomendada o procedimiento de reparación/diagnóstico.
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val modelo: String,
    val codigoAlarma: String,
    val descripcion: String,
    val solucion: String = ""
)
