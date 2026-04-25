package com.example.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registro.data.UnitDao
import com.example.registro.data.UnitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * El ViewModel es el encargado de gestionar los datos para la interfaz de usuario.
 * Ahora incluye manejo de errores para alertas de duplicados.
 */
class UnitViewModel(private val unitDao: UnitDao) : ViewModel() {

    // Estado para manejar mensajes de error/alerta en la UI
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

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
        // Limpiamos errores previos
        _errorMessage.value = null

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
                onSuccess() // Si todo sale bien
            } catch (e: Exception) {
                // Si Room lanza un error (como una violación de unicidad)
                _errorMessage.value = "Error: La Placa o el Número de Unidad ya están registrados."
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
        comentarios: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        
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
                    comentarios = comentarios
                )
                unitDao.insertTemperature(registro)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar la temperatura: ${e.message}"
            }
        }
    }

    /**
     * Limpia el mensaje de error para cerrar la alerta.
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
