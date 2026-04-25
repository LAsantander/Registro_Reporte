package com.example.registro.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.registro.data.AppDatabase
import com.example.registro.ui.screens.CheckListScreen
import com.example.registro.ui.screens.RegistryScreen
import com.example.registro.ui.screens.SearchScreen
import com.example.registro.ui.screens.TemperatureTakingScreen
import com.example.registro.ui.theme.RegistroTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Inicializamos la base de datos y el ViewModel
    val database = AppDatabase.getDatabase(context)
    val viewModel: UnitViewModel = viewModel(
        factory = UnitViewModelFactory(database.unitDao())
    )

    NavHost(navController = navController, startDestination = "search") {
        // Vista 1: Búsqueda (Ahora Menú Principal)
        composable("search") {
            SearchScreen(
                onNavigateToTemperature = { navController.navigate("temperature") },
                onNavigateToRegistry = { navController.navigate("registry") },
                onNavigateToChecklist = { navController.navigate("checklist") }
            )
        }
        
        // Vista 2: Toma de Temperaturas
        composable("temperature") {
            TemperatureTakingScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Vista 3: Registro de Unidad (Aquí es donde se guardan los datos)
        composable("registry") {
            RegistryScreen(viewModel = viewModel)
        }

        // Vista 4: Checklist
        composable("checklist") {
            CheckListScreen()
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
            onNavigateToChecklist = {}
        )
    }
}
