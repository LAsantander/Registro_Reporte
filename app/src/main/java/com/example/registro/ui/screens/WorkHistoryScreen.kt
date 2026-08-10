package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.data.WorkReportEntity
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.theme.RegistroTheme

/**
 * Pantalla de Historial de Trabajos.
 * Muestra una bitácora detallada de todas las intervenciones realizadas a una unidad específica.
 */
@Composable
fun WorkHistoryScreen(
    viewModel: UnitViewModel,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val historial by viewModel.historialTrabajoFiltrado.collectAsState()

    // Búsqueda automática al escribir
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.cargarHistorialPorPlaca(searchQuery)
        }
    }

    WorkHistoryContent(
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        historial = historial,
        onBackClick = onBackClick
    )
}

/**
 * Contenido sin estado de la pantalla de Historial de Trabajos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkHistoryContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    historial: List<WorkReportEntity>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de Trabajos", color = Color.White) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(44.dp)
                            .background(Color(0xFF52A8EE), shape = RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF052A50))
            )
        },
        containerColor = Color(0xFF052A50)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Ingresa Placa para consultar", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White, focusedLabelColor = Color.White, unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (historial.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isBlank()) "Ingresa una placa para ver su bitácora" else "No se encontraron reportes para esta unidad",
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(historial) { report ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = report.tipoTrabajo,
                                            color = Color(0xFF52A8EE),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        if (!report.ot.isNullOrBlank()) {
                                            Text(
                                                text = "OT: ${report.ot}",
                                                color = Color(0xFF52A8EE).copy(alpha = 0.8f),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        if (!report.modeloUnidad.isNullOrBlank()) {
                                            Text(
                                                text = "Modelo: ${report.modeloUnidad}",
                                                color = Color.White.copy(alpha = 0.7f),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                    Text(
                                        text = report.fechaHora.split(" ")[0],
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = report.descripcion,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                
                                if (report.repuestos.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Repuestos: ${report.repuestos}",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = "Técnico: ${report.tecnico}",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkHistoryScreenPreview() {
    RegistroTheme {
        WorkHistoryContent(
            searchQuery = "ABC-123",
            onSearchQueryChange = {},
            historial = listOf(
                WorkReportEntity(
                    id = 1,
                    placa = "ABC-123",
                    ot = "OT-001",
                    modeloUnidad = "Carrier Vector 8500",
                    tipoTrabajo = "Mantenimiento Preventivo",
                    descripcion = "Cambio de aceite y filtros de motor.",
                    tecnico = "Juan Pérez",
                    repuestos = "Filtro aceite, Aceite 15W40",
                    fechaHora = "24/07/2026 10:30:00"
                ),
                WorkReportEntity(
                    id = 2,
                    placa = "ABC-123",
                    ot = "OT-045",
                    modeloUnidad = "Carrier Vector 8500",
                    tipoTrabajo = "Reparación Eléctrica",
                    descripcion = "Revisión de cableado de sensores.",
                    tecnico = "Carlos Gómez",
                    repuestos = "Cinta aislante, terminales",
                    fechaHora = "20/07/2026 15:45:00"
                )
            ),
            onBackClick = {}
        )
    }
}
