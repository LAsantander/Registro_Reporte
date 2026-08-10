package com.example.registro.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.registro.data.AppDatabase
import com.example.registro.data.SettingsRepository
import com.example.registro.ui.screens.CheckListScreen
import com.example.registro.ui.screens.HistoryScreen
import com.example.registro.ui.screens.RegistryScreen
import com.example.registro.ui.screens.SearchScreen
import com.example.registro.ui.screens.TechnicianDataScreen
import com.example.registro.ui.screens.TemperatureTakingScreen
import com.example.registro.ui.screens.WorkHistoryScreen
import com.example.registro.ui.screens.WorkReportScreen
import com.example.registro.ui.theme.RegistroTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Inicializamos la base de datos, el repositorio de configuraciones y el ViewModel
    val database = AppDatabase.getDatabase(context)
    val settingsRepository = SettingsRepository(context)
    val viewModel: UnitViewModel = viewModel(
        factory = UnitViewModelFactory(database.unitDao(), settingsRepository)
    )

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onNavigateToTemperature = { navController.navigate("temperature") },
                onNavigateToRegistry = { navController.navigate("registry") },
                onNavigateToChecklist = { navController.navigate("checklist") },
                onNavigateToWorkReport = { navController.navigate("work_report") },
                onNavigateToWorkHistory = { navController.navigate("work_history") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToTechData = { navController.navigate("tech_data") }
            )
        }
        
        // Vista: Datos del Técnico (QR)
        composable("tech_data") {
            TechnicianDataScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Vista 2: Toma de Temperaturas
        composable("temperature") {
            TemperatureTakingScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista de Reportes de Trabajo
        composable("work_report") {
            WorkReportScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista de Historial de Trabajos
        composable("work_history") {
            WorkHistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista 3: Registro de Unidad (Aquí es donde se guardan los datos)
        composable("registry") {
            RegistryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista 4: Checklist
        composable("checklist") {
            CheckListScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista 5: Historial
        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    RegistroTheme {
        // Previsualizamos la pantalla inicial (Menú Principal)
        SearchScreen(
            onNavigateToTemperature = {},
            onNavigateToRegistry = {},
            onNavigateToChecklist = {},
            onNavigateToWorkReport = {},
            onNavigateToWorkHistory = {},
            onNavigateToHistory = {},
            onNavigateToTechData = {}
        )
    }
}
