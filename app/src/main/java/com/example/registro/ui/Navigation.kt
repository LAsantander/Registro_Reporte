package com.example.registro.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.registro.model.RefrigeratedUnit
import com.example.registro.ui.screens.AddUnitScreen
import com.example.registro.ui.screens.SearchScreen
import com.example.registro.ui.screens.UnitDetailScreen
import com.example.registro.ui.screens.UnitListScreen
import java.util.UUID

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Mock data for demonstration
    var units by remember { 
        mutableStateOf(
            listOf(
                RefrigeratedUnit("1", "ABC-123", 4.5, "Activo", "2023-10-27 10:00"),
                RefrigeratedUnit("2", "XYZ-789", -18.2, "Mantenimiento", "2023-10-27 09:30")
            )
        )
    }

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                onSearchClick = { query ->
                    // Por ahora navegamos a la lista filtrada o general
                    navController.navigate("list")
                }
            )
        }
        composable("list") {
            UnitListScreen(
                units = units,
                onAddUnitClick = { navController.navigate("add") },
                onUnitClick = { unit -> 
                    navController.navigate("detail/${unit.id}")
                }
            )
        }
        composable("add") {
            AddUnitScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveUnit = { plate, temp ->
                    val newUnit = RefrigeratedUnit(
                        id = UUID.randomUUID().toString(),
                        plateNumber = plate,
                        temperature = temp.toDoubleOrNull() ?: 0.0,
                        status = "Activo",
                        lastUpdate = "Recién registrado"
                    )
                    units = units + newUnit
                    navController.popBackStack()
                }
            )
        }
        composable("detail/{unitId}") { backStackEntry ->
            val unitId = backStackEntry.arguments?.getString("unitId")
            val unit = units.find { it.id == unitId }
            UnitDetailScreen(
                unit = unit,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
