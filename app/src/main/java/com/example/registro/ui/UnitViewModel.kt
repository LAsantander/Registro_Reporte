package com.example.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.registro.data.UnitDao
import com.example.registro.data.UnitEntity
import com.example.registro.data.SettingsRepository
import com.example.registro.data.UserSettings
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
import java.util.Calendar

/**
 * Representa las jornadas de trabajo disponibles.
 */
enum class Jornada(val nombre: String, val horaInicio: Int, val horaFin: Int) {
    MANANA("Mañana", 6, 13),
    TARDE("Tarde", 14, 21),
    NOCHE("Noche", 22, 5);

    companion object {
        fun obtenerActual(): Jornada {
            val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return when (horaActual) {
                in 6..13 -> MANANA
                in 14..21 -> TARDE
                else -> NOCHE
            }
        }
    }
}

/**
 * El ViewModel es el encargado de gestionar los datos para la interfaz de usuario.
 * Ahora incluye manejo de errores para alertas de duplicados y gestión de configuraciones.
 */
class UnitViewModel(
    private val unitDao: UnitDao,
    private val settingsRepository: SettingsRepository? = null
) : ViewModel() {

    // Observar las configuraciones de usuario
    val userSettings: StateFlow<UserSettings> = settingsRepository?.userSettingsFlow
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
        ?: MutableStateFlow(UserSettings())

    fun updateShowTemperature(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowTemperature(show) }
    fun updateShowRegistry(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowRegistry(show) }
    fun updateShowChecklist(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowChecklist(show) }
    fun updateShowWorkReport(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowWorkReport(show) }
    fun updateShowWorkHistory(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowWorkHistory(show) }
    fun updateShowHistory(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowHistory(show) }

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

    // Observar reportes de trabajo recientes
    val reportesTrabajoRecientes: StateFlow<List<com.example.registro.data.WorkReportEntity>> = 
        unitDao.getRecentWorkReports()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado para el historial de trabajo filtrado por una unidad específica
    private val _historialTrabajoFiltrado = MutableStateFlow<List<com.example.registro.data.WorkReportEntity>>(emptyList())
    val historialTrabajoFiltrado: StateFlow<List<com.example.registro.data.WorkReportEntity>> = _historialTrabajoFiltrado

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
     * Filtra una lista de registros por una jornada específica.
     */
    fun filtrarRegistrosPorJornada(
        registros: List<com.example.registro.data.TemperatureEntity>,
        jornada: Jornada
    ): List<com.example.registro.data.TemperatureEntity> {
        return registros.filter { reg ->
            // El formato de fechaHora es "dd/MM/yyyy HH:mm:ss"
            val partes = reg.fechaHora.split(" ")
            if (partes.size < 2) return@filter false
            
            val hora = partes[1].split(":")[0].toIntOrNull() ?: return@filter false
            
            if (jornada == Jornada.NOCHE) {
                // La noche abarca de 22:00 a 05:59 (cruza la medianoche)
                hora >= 22 || hora <= 5
            } else {
                hora in jornada.horaInicio..jornada.horaFin
            }
        }
    }

    /**
     * Guarda un reporte de trabajo.
     */
    fun guardarReporteTrabajo(
        placa: String,
        numeroUnidad: String,
        tipoTrabajo: String,
        descripcion: String,
        tecnico: String,
        repuestos: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank() || numeroUnidad.isBlank() || tipoTrabajo.isBlank() || descripcion.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios del reporte."
            return
        }

        viewModelScope.launch {
            try {
                val reporte = com.example.registro.data.WorkReportEntity(
                    placa = placa.uppercase().trim(),
                    numeroUnidad = numeroUnidad.trim(),
                    tipoTrabajo = tipoTrabajo,
                    descripcion = descripcion,
                    tecnico = tecnico,
                    repuestos = repuestos
                )
                unitDao.insertWorkReport(reporte)
                _successMessage.value = "Reporte de trabajo guardado con éxito."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar el reporte: ${e.message}"
            }
        }
    }

    /**
     * Obtiene reportes de trabajo por placa y actualiza el estado del historial filtrado.
     */
    fun cargarHistorialPorPlaca(placa: String) {
        viewModelScope.launch {
            _historialTrabajoFiltrado.value = unitDao.getWorkReportsByUnit(placa.uppercase().trim())
        }
    }

    /**
     * Obtiene reportes de trabajo por placa.
     */
    suspend fun obtenerReportesPorUnidad(placa: String): List<com.example.registro.data.WorkReportEntity> {
        return unitDao.getWorkReportsByUnit(placa.uppercase().trim())
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
                val workReports = unitDao.getAllWorkReportsList()
                val hoy = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                
                val backup = BackupData(
                    units = units,
                    temperatures = temps,
                    workReports = workReports,
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
                    // Preparamos los datos para la fusión: Reiniciamos IDs para evitar sobrescribir por ID local.
                    // Las unidades se fusionarán o actualizarán basándose en su PLACA única.
                    val unitsToInsert = backup.units.map { it.copy(id = 0) }
                    
                    // Los registros de temperatura y reportes se insertarán como registros nuevos (se sumarán al historial local).
                    val tempsToInsert = backup.temperatures.map { it.copy(id = 0) }
                    val workReportsToInsert = backup.workReports.map { it.copy(id = 0) }

                    unitDao.insertUnitsList(unitsToInsert)
                    unitDao.insertTemperaturesList(tempsToInsert)
                    unitDao.insertWorkReportsList(workReportsToInsert)
                    
                    _successMessage.value = "Respaldo importado y fusionado con éxito. Se procesaron ${backup.units.size} unidades, ${backup.temperatures.size} registros de temperatura y ${backup.workReports.size} reportes de trabajo."
                } else {
                    _errorMessage.value = "El archivo de respaldo no es válido o está dañado."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al importar: ${e.message}"
            }
        }
    }
}
