package com.example.registro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class UserSettings(
    val showTemperature: Boolean = true,
    val showRegistry: Boolean = true,
    val showChecklist: Boolean = true,
    val showWorkReport: Boolean = true,
    val showWorkHistory: Boolean = true,
    val showHistory: Boolean = true,
    val defaultTechnician: String = "",
    val techCedula: String = "",
    val techEmpresa: String = "",
    val techDestino: String = "",
    val techProfesion: String = "",
    val techAsunto: String = "",
    val techPlaca: String = "",
    val otOnlyNumbers: Boolean = false
)

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val SHOW_TEMPERATURE = booleanPreferencesKey("show_temperature")
        val SHOW_REGISTRY = booleanPreferencesKey("show_registry")
        val SHOW_CHECKLIST = booleanPreferencesKey("show_checklist")
        val SHOW_WORK_REPORT = booleanPreferencesKey("show_work_report")
        val SHOW_WORK_HISTORY = booleanPreferencesKey("show_work_history")
        val SHOW_HISTORY = booleanPreferencesKey("show_history")
        val DEFAULT_TECHNICIAN = stringPreferencesKey("default_technician")
        val TECH_CEDULA = stringPreferencesKey("tech_cedula")
        val TECH_EMPRESA = stringPreferencesKey("tech_empresa")
        val TECH_DESTINO = stringPreferencesKey("tech_destino")
        val TECH_PROFESION = stringPreferencesKey("tech_profesion")
        val TECH_ASUNTO = stringPreferencesKey("tech_asunto")
        val TECH_PLACA = stringPreferencesKey("tech_placa")
        val OT_ONLY_NUMBERS = booleanPreferencesKey("ot_only_numbers")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserSettings(
                showTemperature = preferences[PreferencesKeys.SHOW_TEMPERATURE] ?: true,
                showRegistry = preferences[PreferencesKeys.SHOW_REGISTRY] ?: true,
                showChecklist = preferences[PreferencesKeys.SHOW_CHECKLIST] ?: true,
                showWorkReport = preferences[PreferencesKeys.SHOW_WORK_REPORT] ?: true,
                showWorkHistory = preferences[PreferencesKeys.SHOW_WORK_HISTORY] ?: true,
                showHistory = preferences[PreferencesKeys.SHOW_HISTORY] ?: true,
                defaultTechnician = preferences[PreferencesKeys.DEFAULT_TECHNICIAN] ?: "",
                techCedula = preferences[PreferencesKeys.TECH_CEDULA] ?: "",
                techEmpresa = preferences[PreferencesKeys.TECH_EMPRESA] ?: "",
                techDestino = preferences[PreferencesKeys.TECH_DESTINO] ?: "",
                techProfesion = preferences[PreferencesKeys.TECH_PROFESION] ?: "",
                techAsunto = preferences[PreferencesKeys.TECH_ASUNTO] ?: "",
                techPlaca = preferences[PreferencesKeys.TECH_PLACA] ?: "",
                otOnlyNumbers = preferences[PreferencesKeys.OT_ONLY_NUMBERS] ?: false
            )
        }

    suspend fun updateOtOnlyNumbers(onlyNumbers: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.OT_ONLY_NUMBERS] = onlyNumbers
        }
    }

    suspend fun updateTechnicianData(
        nombre: String,
        cedula: String,
        empresa: String,
        destino: String,
        profesion: String,
        asunto: String,
        placa: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_TECHNICIAN] = nombre
            preferences[PreferencesKeys.TECH_CEDULA] = cedula
            preferences[PreferencesKeys.TECH_EMPRESA] = empresa
            preferences[PreferencesKeys.TECH_DESTINO] = destino
            preferences[PreferencesKeys.TECH_PROFESION] = profesion
            preferences[PreferencesKeys.TECH_ASUNTO] = asunto
            preferences[PreferencesKeys.TECH_PLACA] = placa
        }
    }

    suspend fun updateDefaultTechnician(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_TECHNICIAN] = name
        }
    }

    suspend fun updateShowTemperature(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TEMPERATURE] = show
        }
    }

    suspend fun updateShowRegistry(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_REGISTRY] = show
        }
    }

    suspend fun updateShowChecklist(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_CHECKLIST] = show
        }
    }

    suspend fun updateShowWorkReport(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_WORK_REPORT] = show
        }
    }

    suspend fun updateShowWorkHistory(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_WORK_HISTORY] = show
        }
    }

    suspend fun updateShowHistory(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_HISTORY] = show
        }
    }
}
