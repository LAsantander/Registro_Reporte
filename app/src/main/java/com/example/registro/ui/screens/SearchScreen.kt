package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.theme.RegistroTheme

@Composable
fun SearchScreen(
    viewModel: UnitViewModel? = null,
    onNavigateToTemperature: () -> Unit,
    onNavigateToRegistry: () -> Unit,
    onNavigateToChecklist: () -> Unit,
    onNavigateToHistory: () -> Unit
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
            .background(Color(0xFF052A50)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Columna para organizar los botones verticalmente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menú Principal",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Botones de navegación
            Button(
                onClick = onNavigateToTemperature,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("TOMA DE TEMPERATURA", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToRegistry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("REGISTRO DE UNIDAD", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToChecklist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("INSPECCIÓN TÉCNICA", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("HISTORIAL DE TEMPERATURAS", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones de Respaldo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel?.exportarRespaldo(context) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("EXPORTAR", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { importLauncher.launch("application/json") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("IMPORTAR", fontSize = 12.sp)
                }
            }
        }
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
            onNavigateToHistory = {}
        )
    }
}
