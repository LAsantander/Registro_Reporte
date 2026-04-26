package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.data.TemperatureEntity
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.utils.PrintUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: UnitViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf("") }
    var registrosFiltrados by remember { mutableStateOf<List<TemperatureEntity>>(emptyList()) }

    // Al cargar la pantalla o cambiar la fecha, buscamos los registros
    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotBlank()) {
            registrosFiltrados = viewModel.obtenerRegistrosPorFecha(selectedDate)
        } else {
            // Por defecto muestra lo de hoy
            val hoy = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            selectedDate = hoy
            registrosFiltrados = viewModel.obtenerRegistrosPorFecha(hoy)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF052A50))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera con flecha y título
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
                }
                Text(
                    "Historial de Temperaturas",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Selector de Fecha Simple (Input de texto por ahora para rapidez)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { selectedDate = it },
                label = { Text("Fecha (dd/mm/yyyy)", color = Color.White.copy(0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Imprimir este día
            Button(
                onClick = {
                    PrintUtils.imprimirReporteDelDia(context, registrosFiltrados)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registrosFiltrados.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("IMPRIMIR ESTE DÍA")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de registros
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(registrosFiltrados) { registro ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Placa: ${registro.placa}", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(registro.fechaHora.split(" ").getOrNull(1) ?: "", color = Color.White.copy(0.7f))
                            }
                            Text("Unidad: ${registro.numeroUnidad}", color = Color.White.copy(0.8f))
                            Text("T1: ${registro.temp1}° | T2: ${registro.temp2}°", color = Color.White)
                            if (registro.comentarios.isNotBlank()) {
                                Text("Obs: ${registro.comentarios}", color = Color.White.copy(0.6f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
