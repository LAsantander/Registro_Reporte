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
import com.example.registro.model.BackupData
import android.content.Context
import android.net.Uri
import com.example.registro.ui.utils.BackupUtils

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

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

    // Observar los últimos 5 registros de temperatura del día actual
    val registrosRecientes: StateFlow<List<com.example.registro.data.TemperatureEntity>> = 
        unitDao.getRecentTemperatures(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
        isTemp1Alert: Boolean,
        temp2: String,
        isTemp2Alert: Boolean,
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
                    isTemp1Alert = isTemp1Alert,
                    temp2 = temp2,
                    isTemp2Alert = isTemp2Alert,
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
     * Elimina un registro de temperatura por su ID.
     */
    fun eliminarTemperatura(id: Int) {
        viewModelScope.launch {
            try {
                unitDao.deleteTemperatureById(id)
                _successMessage.value = "Registro eliminado correctamente."
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el registro: ${e.message}"
            }
        }
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
    // --- Estado para Checklist ---
    private val _hallazgosChecklist = MutableStateFlow<List<Pair<String, List<Uri>>>>(emptyList())
    val hallazgosChecklist: StateFlow<List<Pair<String, List<Uri>>>> = _hallazgosChecklist

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Guarda un hallazgo en la lista temporal de la sesión actual.
     */
    fun guardarInspeccion(
        placa: String,
        comentarios: String,
        fotos: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val listaActual = _hallazgosChecklist.value.toMutableList()
            listaActual.add(Pair(comentarios, fotos))
            _hallazgosChecklist.value = listaActual
            
            _successMessage.value = "Punto de inspección registrado"
            onSuccess()
        }
    }

    fun limpiarSesionChecklist() {
        _hallazgosChecklist.value = emptyList()
    }

    // --- Funciones de Respaldo ---

    fun exportarRespaldo(context: Context) {
        viewModelScope.launch {
            try {
                val units = unitDao.getAllUnitsList()
                val temps = unitDao.getAllTemperaturesList()
                val hoy = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                val backup = BackupData(
                    units = units,
                    temperatures = temps,
                    exportDate = hoy
                )
                
                BackupUtils.exportBackup(context, backup)
            } catch (e: Exception) {
                _errorMessage.value = "Error al exportar: ${e.message}"
            }
        }
    }

    fun importarRespaldo(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val backup = BackupUtils.importBackup(context, uri)
                if (backup != null) {
                    // Insertamos los datos en la base de datos (con estrategia REPLACE si ya existen)
                    unitDao.insertUnitsList(backup.units)
                    unitDao.insertTemperaturesList(backup.temperatures)
                    _successMessage.value = "Respaldo importado con éxito. Se cargaron ${backup.units.size} unidades y ${backup.temperatures.size} registros."
                } else {
                    _errorMessage.value = "El archivo de respaldo no es válido o está dañado."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al importar: ${e.message}"
            }
        }
    }
}
