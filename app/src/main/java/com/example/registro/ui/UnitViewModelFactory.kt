package com.example.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.registro.data.UnitDao

/**
 * Clase necesaria para pasarle el DAO al ViewModel.
 */
class UnitViewModelFactory(private val unitDao: UnitDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnitViewModel(unitDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
