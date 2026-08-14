package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.theme.RegistroTheme
import com.example.registro.data.UserSettings

/**
 * Modelo que representa una opción interactiva en la cuadrícula del menú principal.
 */
data class MenuOption(
    val title: String,
    val icon: ImageVector,
    val isVisible: Boolean,
    val onClick: () -> Unit
)

/**
 * Pantalla del Menú Principal (Dashboard).
 * Permite la navegación a las diferentes funcionalidades de la app
 * y la gestión de respaldos.
 */
@Composable
fun SearchScreen(
    viewModel: UnitViewModel? = null,
    onNavigateToTemperature: () -> Unit,
    onNavigateToRegistry: () -> Unit,
    onNavigateToChecklist: () -> Unit,
    onNavigateToWorkReport: () -> Unit,
    onNavigateToWorkHistory: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTechData: () -> Unit
) {
    val context = LocalContext.current
    
    // Launcher para seleccionar el archivo de respaldo
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel?.importarRespaldo(context, it) }
    }

    // Observar mensajes del ViewModel para las alertas
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })

    // Observar configuraciones
    val userSettings by (viewModel?.userSettings?.collectAsState() ?: remember { mutableStateOf(UserSettings()) })
    var showSettingsDialog by remember { mutableStateOf(false) }

    val menuOptions = listOf(
        MenuOption("TOMA DE TEMPERATURA", Icons.Default.Thermostat, userSettings.showTemperature, onNavigateToTemperature),
        MenuOption("REGISTRO DE UNIDAD", Icons.Default.DirectionsBus, userSettings.showRegistry, onNavigateToRegistry),
        MenuOption("INSPECCIÓN TÉCNICA", Icons.Default.FactCheck, userSettings.showChecklist, onNavigateToChecklist),
        MenuOption("REPORTES DE TRABAJO", Icons.Default.Build, userSettings.showWorkReport, onNavigateToWorkReport),
        MenuOption("HISTORIAL DE TRABAJOS", Icons.Default.History, userSettings.showWorkHistory, onNavigateToWorkHistory),
        MenuOption("HISTORIAL TEMPERATURAS", Icons.Default.DeviceThermostat, userSettings.showHistory, onNavigateToHistory),
        MenuOption("DATOS DEL TÉCNICO", Icons.Default.Badge, true, onNavigateToTechData)
    ).filter { it.isVisible }

    // Mostrar Alerta si hay un error
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Aviso") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) {
                    Text("Entendido")
                }
            }
        )
    }

    // Mostrar Alerta si el éxito
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Éxito") },
            text = { Text(successMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Contenedor principal que ocupa toda la pantalla con fondo azul oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF052A50))
    ) {
        // Icono de Configuración
        IconButton(
            onClick = { showSettingsDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .padding(top = 24.dp)
        ) {
            Icon(Icons.Default.Settings, "Configuración", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menú Principal",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Cuadrícula de opciones
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(menuOptions) { option ->
                    MenuCard(option)
                }
            }

            // Botones de Respaldo al final
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel?.exportarRespaldo(context) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileUpload, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("EXPORTAR", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { importLauncher.launch("application/json") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("IMPORTAR", fontSize = 12.sp)
                }
            }
        }
    }

    // Diálogo de Configuración
    if (showSettingsDialog) {
        var localTechName by remember { mutableStateOf(userSettings.defaultTechnician) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Configuración de Menú") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona las funciones que deseas ver:", fontSize = 14.sp)
                    
                    SettingToggle("Toma de Temperatura", userSettings.showTemperature) {
                        viewModel?.updateShowTemperature(it)
                    }
                    SettingToggle("Registro de Unidad", userSettings.showRegistry) {
                        viewModel?.updateShowRegistry(it)
                    }
                    SettingToggle("Inspección Técnica", userSettings.showChecklist) {
                        viewModel?.updateShowChecklist(it)
                    }
                    SettingToggle("Reportes de Trabajo", userSettings.showWorkReport) {
                        viewModel?.updateShowWorkReport(it)
                    }
                    SettingToggle("Historial de Trabajos", userSettings.showWorkHistory) {
                        viewModel?.updateShowWorkHistory(it)
                    }
                    SettingToggle("Historial de Temperaturas", userSettings.showHistory) {
                        viewModel?.updateShowHistory(it)
                    }

                    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text("Configuración de Técnico:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = localTechName,
                        onValueChange = { 
                            localTechName = it
                            viewModel?.updateDefaultTechnician(it) 
                        },
                        label = { Text("Nombre del Técnico (Predeterminado)", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )

                    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                    SettingToggle("OT: Solo números", userSettings.otOnlyNumbers) {
                        viewModel?.updateOtOnlyNumbers(it)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

/**
 * Tarjeta interactiva estilizada para las opciones del menú principal.
 */
@Composable
fun MenuCard(option: MenuOption) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { option.onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF52A8EE).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = Color(0xFF52A8EE),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = option.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

/**
 * Componente de fila con etiqueta e interruptor para el menú de configuración.
 */
@Composable
fun SettingToggle(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    RegistroTheme {
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
