package com.example.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.registro.data.UnitDao
import com.example.registro.data.SettingsRepository

/**
 * Clase necesaria para pasarle el DAO y el Repositorio de configuraciones al ViewModel.
 */
class UnitViewModelFactory(
    private val unitDao: UnitDao,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnitViewModel(unitDao, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
