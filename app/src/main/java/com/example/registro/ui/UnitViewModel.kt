package com.example.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registro.data.UnitDao
import com.example.registro.data.UnitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * El ViewModel es el encargado de gestionar los datos para la interfaz de usuario.
 * Ahora incluye manejo de errores para alertas de duplicados.
 */
class UnitViewModel(private val unitDao: UnitDao) : ViewModel() {

    // Estado para manejar mensajes de error/alerta en la UI
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Estado para manejar mensajes de éxito
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    /**
     * Función para insertar una nueva unidad con validación de duplicados.
     */
    fun guardarUnidad(
        placa: String,
        numeroUnidad: String,
        marca: String,
        modelo: String,
        serie: String,
        onSuccess: () -> Unit
    ) {
        // Limpiamos mensajes previos
        _errorMessage.value = null
        _successMessage.value = null

        // Validación básica
        if (placa.isBlank() || numeroUnidad.isBlank() || marca.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios."
            return
        }

        viewModelScope.launch {
            try {
                val nuevaUnidad = UnitEntity(
                    placa = placa.uppercase().trim(),
                    numeroUnidad = numeroUnidad.trim(),
                    marca = marca,
                    modelo = modelo,
                    serie = serie
                )
                unitDao.insertUnit(nuevaUnidad)
                _successMessage.value = "Unidad registrada correctamente."
                onSuccess() // Si todo sale bien
            } catch (e: Exception) {
                // Si Room lanza un error (como una violación de unicidad)
                _errorMessage.value = "Error: La Placa o el Número de Unidad ya están registrados."
            }
        }
    }

    /**
     * Función para actualizar una unidad existente.
     */
    fun actualizarUnidad(
        id: Int,
        placa: String,
        numeroUnidad: String,
        marca: String,
        modelo: String,
        serie: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank() || numeroUnidad.isBlank() || marca.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios."
            return
        }

        viewModelScope.launch {
            try {
                val unidadActualizada = UnitEntity(
                    id = id,
                    placa = placa.uppercase().trim(),
                    numeroUnidad = numeroUnidad.trim(),
                    marca = marca,
                    modelo = modelo,
                    serie = serie
                )
                unitDao.updateUnit(unidadActualizada)
                _successMessage.value = "Datos de la unidad actualizados con éxito."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: La Placa o el Número de Unidad ya existen."
            }
        }
    }

    /**
     * Busca una unidad en la base de datos por placa o ID.
     */
    suspend fun buscarUnidad(query: String): UnitEntity? {
        return unitDao.findUnitByPlacaOrId(query.uppercase().trim())
    }

    /**
     * Guarda un registro de temperatura en la base de datos.
     */
    fun guardarTemperatura(
        placa: String,
        numeroUnidad: String,
        temp1: String,
        temp2: String,
        unidadTemp: String,
        comentarios: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null
        
        if (placa.isBlank() || numeroUnidad.isBlank() || temp1.isBlank() || temp2.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos de temperatura."
            return
        }

        viewModelScope.launch {
            try {
                val registro = com.example.registro.data.TemperatureEntity(
                    placa = placa.uppercase().trim(),
                    numeroUnidad = numeroUnidad.trim(),
                    temp1 = temp1,
                    temp2 = temp2,
                    unidadTemp = unidadTemp,
                    comentarios = comentarios
                )
                unitDao.insertTemperature(registro)
                _successMessage.value = "Toma de temperatura guardada con éxito."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar la temperatura: ${e.message}"
            }
        }
    }

    /**
     * Obtiene los registros de temperatura de una fecha específica.
     */
    suspend fun obtenerRegistrosPorFecha(fecha: String): List<com.example.registro.data.TemperatureEntity> {
        return unitDao.getTemperaturesByDate(fecha.trim())
    }

    /**
     * Obtiene los registros de temperatura del día actual.
     */
    suspend fun obtenerRegistrosDelDia(): List<com.example.registro.data.TemperatureEntity> {
        val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        return unitDao.getTemperaturesByDate(hoy)
    }

    /**
     * Limpia los mensajes para cerrar las alertas.
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    /**
     * Limpia el mensaje de error para cerrar la alerta.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
