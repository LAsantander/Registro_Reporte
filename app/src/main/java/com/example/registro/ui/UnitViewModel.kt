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
 * Representa las jornadas de trabajo disponibles basadas en la hora del sistema.
 * Ayuda a filtrar los reportes impresos o compartidos por turno.
 */
enum class Jornada(val nombre: String, val horaInicio: Int, val horaFin: Int) {
    MANANA("Mañana", 6, 13),
    TARDE("Tarde", 14, 21),
    NOCHE("Noche", 22, 5);

    companion object {
        /**
         * Determina automáticamente en qué jornada se encuentra el sistema actualmente.
         */
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
 * El ViewModel central encargado de gestionar los datos para la interfaz de usuario.
 * Actúa como puente entre el DAO (Room) y las pantallas de Jetpack Compose.
 */
class UnitViewModel(
    private val unitDao: UnitDao,
    private val settingsRepository: SettingsRepository? = null
) : ViewModel() {

    /**
     * Estado observable de las configuraciones de usuario (visibilidad de botones).
     */
    val userSettings: StateFlow<UserSettings> = settingsRepository?.userSettingsFlow
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())
        ?: MutableStateFlow(UserSettings())

    /**
     * Flujo reactivo con las placas que coinciden con una búsqueda parcial.
     */
    private val _sugerenciasPlacas = MutableStateFlow<List<String>>(emptyList())
    val sugerenciasPlacas: StateFlow<List<String>> = _sugerenciasPlacas

    /**
     * Actualiza la lista de sugerencias de placas basadas en un texto de búsqueda.
     */
    fun buscarSugerenciasPlacas(query: String) {
        if (query.isBlank()) {
            _sugerenciasPlacas.value = emptyList()
            return
        }
        viewModelScope.launch {
            unitDao.searchByPlaca("%${query.uppercase()}%").collect { units ->
                _sugerenciasPlacas.value = units.map { it.placa }
            }
        }
    }
    
    // Funciones rápidas para actualizar preferencias desde la UI
    fun updateShowTemperature(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowTemperature(show) }
    fun updateShowRegistry(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowRegistry(show) }
    fun updateShowChecklist(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowChecklist(show) }
    fun updateShowWorkReport(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowWorkReport(show) }
    fun updateShowWorkHistory(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowWorkHistory(show) }
    fun updateShowHistory(show: Boolean) = viewModelScope.launch { settingsRepository?.updateShowHistory(show) }
    fun updateDefaultTechnician(name: String) = viewModelScope.launch { settingsRepository?.updateDefaultTechnician(name) }
    fun updateOtOnlyNumbers(onlyNumbers: Boolean) = viewModelScope.launch { settingsRepository?.updateOtOnlyNumbers(onlyNumbers) }
    
    fun updateTechnicianData(
        nombre: String,
        cedula: String,
        empresa: String,
        destino: String,
        profesion: String,
        asunto: String,
        placa: String
    ) = viewModelScope.launch {
        settingsRepository?.updateTechnicianData(nombre, cedula, empresa, destino, profesion, asunto, placa)
    }

    // Estados para manejar mensajes de alerta y éxito en la UI
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    /**
     * Observar los últimos 5 registros de temperatura del día actual.
     */
    val registrosRecientes: StateFlow<List<com.example.registro.data.TemperatureEntity>> = 
        unitDao.getRecentTemperatures(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Observar los 10 reportes de trabajo globales más recientes.
     */
    val reportesTrabajoRecientes: StateFlow<List<com.example.registro.data.WorkReportEntity>> = 
        unitDao.getRecentWorkReports()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Estado para el historial de trabajo filtrado por una unidad específica.
     */
    private val _historialTrabajoFiltrado = MutableStateFlow<List<com.example.registro.data.WorkReportEntity>>(emptyList())
    val historialTrabajoFiltrado: StateFlow<List<com.example.registro.data.WorkReportEntity>> = _historialTrabajoFiltrado

    /**
     * Registra una nueva unidad técnica con validación.
     */
    fun guardarUnidad(
        placa: String,
        numeroUnidad: String,
        marca: String,
        modelo: String,
        serie: String,
        voltaje: String,
        empresa: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank()) {
            _errorMessage.value = "Por favor ingresa al menos la Placa de la unidad."
            return
        }

        viewModelScope.launch {
            try {
                val nuevaUnidad = UnitEntity(
                    placa = placa.uppercase().trim(),
                    numeroUnidad = if (numeroUnidad.isBlank()) null else numeroUnidad.trim(),
                    marca = marca,
                    modelo = modelo,
                    serie = serie,
                    voltaje = voltaje,
                    empresa = empresa.trim()
                )
                unitDao.insertUnit(nuevaUnidad)
                _successMessage.value = "Unidad registrada correctamente."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error: La Placa ya está registrada en el sistema."
            }
        }
    }

    /**
     * Actualiza la información de una unidad existente.
     */
    fun actualizarUnidad(
        id: Int,
        placa: String,
        numeroUnidad: String,
        marca: String,
        modelo: String,
        serie: String,
        voltaje: String,
        empresa: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank()) {
            _errorMessage.value = "La placa es obligatoria para actualizar."
            return
        }

        viewModelScope.launch {
            try {
                val unidadActualizada = UnitEntity(
                    id = id,
                    placa = placa.uppercase().trim(),
                    numeroUnidad = if (numeroUnidad.isBlank()) null else numeroUnidad.trim(),
                    marca = marca,
                    modelo = modelo,
                    serie = serie,
                    voltaje = voltaje,
                    empresa = empresa.trim()
                )
                unitDao.updateUnit(unidadActualizada)
                _successMessage.value = "Datos de la unidad actualizados con éxito."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: Conflicto con los datos registrados."
            }
        }
    }

    /**
     * Localiza una unidad por coincidencia exacta de placa o identificador de empresa.
     */
    suspend fun buscarUnidad(query: String): UnitEntity? {
        return unitDao.findUnitByPlacaOrId(query.uppercase().trim())
    }

    /**
     * Almacena una nueva toma de temperatura en la base de datos.
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
        
        if (placa.isBlank() || temp1.isBlank() || temp2.isBlank()) {
            _errorMessage.value = "Por favor completa la Placa y las temperaturas."
            return
        }

        viewModelScope.launch {
            try {
                val registro = com.example.registro.data.TemperatureEntity(
                    placa = placa.uppercase().trim(),
                    numeroUnidad = if (numeroUnidad.isBlank()) null else numeroUnidad.trim(),
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
     * Recupera todos los registros de temperatura guardados en una fecha específica.
     */
    suspend fun obtenerRegistrosPorFecha(fecha: String): List<com.example.registro.data.TemperatureEntity> {
        return unitDao.getTemperaturesByDate(fecha.trim())
    }

    /**
     * Recupera todos los registros de temperatura del día actual.
     */
    suspend fun obtenerRegistrosDelDia(): List<com.example.registro.data.TemperatureEntity> {
        val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        return unitDao.getTemperaturesByDate(hoy)
    }

    /**
     * Filtra una lista de registros para que coincidan con una jornada laboral específica.
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
     * Crea un nuevo reporte de servicio técnico.
     */
    fun guardarReporteTrabajo(
        placa: String,
        ot: String,
        modeloUnidad: String,
        tipoTrabajo: String,
        descripcion: String,
        tecnico: String,
        repuestos: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank() || ot.isBlank() || tipoTrabajo.isBlank() || descripcion.isBlank()) {
            _errorMessage.value = "Por favor completa la Placa, OT, el Tipo y la Descripción."
            return
        }

        viewModelScope.launch {
            try {
                val reporte = com.example.registro.data.WorkReportEntity(
                    placa = placa.uppercase().trim(),
                    ot = ot.trim(),
                    modeloUnidad = if (modeloUnidad.isBlank()) null else modeloUnidad.trim(),
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
     * Actualiza un reporte de trabajo que ya existe en la base de datos.
     */
    fun actualizarReporteTrabajo(
        id: Int,
        placa: String,
        ot: String,
        modeloUnidad: String,
        tipoTrabajo: String,
        descripcion: String,
        tecnico: String,
        repuestos: String,
        onSuccess: () -> Unit
    ) {
        _errorMessage.value = null
        _successMessage.value = null

        if (placa.isBlank() || ot.isBlank() || tipoTrabajo.isBlank() || descripcion.isBlank()) {
            _errorMessage.value = "Por favor completa los campos obligatorios (Placa, OT, Tipo, Descripción)."
            return
        }

        viewModelScope.launch {
            try {
                val reporte = com.example.registro.data.WorkReportEntity(
                    id = id,
                    placa = placa.uppercase().trim(),
                    ot = ot.trim(),
                    modeloUnidad = if (modeloUnidad.isBlank()) null else modeloUnidad.trim(),
                    tipoTrabajo = tipoTrabajo,
                    descripcion = descripcion,
                    tecnico = tecnico,
                    repuestos = repuestos
                )
                unitDao.updateWorkReport(reporte)
                _successMessage.value = "Reporte de trabajo actualizado con éxito."
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar el reporte: ${e.message}"
            }
        }
    }

    /**
     * Borra permanentemente un reporte de trabajo.
     */
    fun eliminarReporteTrabajo(id: Int) {
        viewModelScope.launch {
            try {
                unitDao.deleteWorkReportById(id)
                _successMessage.value = "Reporte eliminado correctamente."
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el reporte: ${e.message}"
            }
        }
    }

    /**
     * Obtiene reportes de trabajo por placa.
     */
    suspend fun obtenerReportesPorUnidad(placa: String): List<com.example.registro.data.WorkReportEntity> {
        return unitDao.getWorkReportsByUnit(placa.uppercase().trim())
    }

    /**
     * Borra permanentemente una toma de temperatura.
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
     * Cierra las ventanas de diálogo de alerta limpiando los mensajes de estado.
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // --- Estado para Checklist (Temporal por sesión) ---
    private val _hallazgosChecklist = MutableStateFlow<List<Pair<String, List<Uri>>>>(emptyList())
    val hallazgosChecklist: StateFlow<List<Pair<String, List<Uri>>>> = _hallazgosChecklist

    /**
     * Registra un nuevo hallazgo con comentario y fotos en la sesión actual de inspección.
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

    /**
     * Limpia la memoria temporal de la inspección actual.
     */
    fun limpiarSesionChecklist() {
        _hallazgosChecklist.value = emptyList()
    }

    // --- Funciones de Respaldo (Persistencia Externa) ---

    /**
     * Agrupa todos los datos locales en un objeto BackupData y activa el menú de compartir.
     */
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

    /**
     * Procesa un archivo JSON seleccionado para fusionar sus datos con la base de datos local.
     */
    fun importarRespaldo(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val backup = BackupUtils.importBackup(context, uri)
                if (backup != null) {
                    // Reiniciamos IDs para asegurar la fusión de registros históricos
                    val unitsToInsert = backup.units.map { oldUnit ->
                        UnitEntity(
                            id = 0,
                            placa = oldUnit.placa,
                            numeroUnidad = oldUnit.numeroUnidad,
                            marca = oldUnit.marca ?: "",
                            modelo = oldUnit.modelo ?: "",
                            serie = oldUnit.serie ?: "",
                            voltaje = oldUnit.voltaje ?: "",
                            empresa = oldUnit.empresa ?: ""
                        )
                    }
                    
                    val tempsToInsert = backup.temperatures.map { oldTemp ->
                        oldTemp.copy(
                            id = 0,
                            numeroUnidad = oldTemp.numeroUnidad
                        )
                    }
                    val workReportsToInsert = backup.workReports.map { oldReport ->
                        oldReport.copy(
                            id = 0,
                            ot = oldReport.ot ?: "",
                            modeloUnidad = oldReport.modeloUnidad
                        )
                    }

                    unitDao.insertUnitsList(unitsToInsert)
                    unitDao.insertTemperaturesList(tempsToInsert)
                    unitDao.insertWorkReportsList(workReportsToInsert)
                    
                    _successMessage.value = "Respaldo importado y fusionado con éxito."
                } else {
                    _errorMessage.value = "El archivo de respaldo no es válido o está dañado."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al importar: ${e.message}"
            }
        }
    }
}
