package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.theme.RegistroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkReportScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var numeroUnidad by remember { mutableStateOf("") }
    var tipoTrabajo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }
    var repuestos by remember { mutableStateOf("") }

    val reportesRecientes by (viewModel?.reportesTrabajoRecientes?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })

    // Lógica de búsqueda automática
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && viewModel != null) {
            val unidadEncontrada = viewModel.buscarUnidad(searchQuery)
            if (unidadEncontrada != null) {
                placa = unidadEncontrada.placa
                numeroUnidad = unidadEncontrada.numeroUnidad
            }
        }
    }

    // Alertas
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Aviso") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) { Text("Entendido") }
            },
            containerColor = Color(0xFFB71C1C),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Éxito") },
            text = { Text(successMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) { Text("Aceptar") }
            },
            containerColor = Color(0xFF1B5E20),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reporte de Trabajo", color = Color.White) },
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
                .imePadding()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar unidad (Placa o ID)", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White, focusedLabelColor = Color.White, unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            // Datos Unidad (Placa y Número)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = placa, onValueChange = { placa = it },
                    label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
                )
                OutlinedTextField(
                    value = numeroUnidad, onValueChange = { numeroUnidad = it },
                    label = { Text("Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
                )
            }

            // Tipo de Trabajo
            var expanded by remember { mutableStateOf(false) }
            val tipos = listOf("Mantenimiento Preventivo", "Mantenimiento Correctivo", "Reparación Eléctrica", "Reparación Mecánica", "Otro")
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = tipoTrabajo, onValueChange = {}, readOnly = true,
                    label = { Text("Tipo de Trabajo", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF052A50))) {
                    tipos.forEach { tip ->
                        DropdownMenuItem(text = { Text(tip, color = Color.White) }, onClick = { tipoTrabajo = tip; expanded = false })
                    }
                }
            }

            // Descripción
            OutlinedTextField(
                value = descripcion, onValueChange = { descripcion = it },
                label = { Text("Descripción del Trabajo", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            // Técnico y Repuestos
            OutlinedTextField(
                value = tecnico, onValueChange = { tecnico = it },
                label = { Text("Técnico Asignado", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            OutlinedTextField(
                value = repuestos, onValueChange = { repuestos = it },
                label = { Text("Repuestos / Materiales", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            // Botón Guardar
            Button(
                onClick = {
                    viewModel?.guardarReporteTrabajo(placa, numeroUnidad, tipoTrabajo, descripcion, tecnico, repuestos) {
                        searchQuery = ""; placa = ""; numeroUnidad = ""; tipoTrabajo = ""; descripcion = ""; tecnico = ""; repuestos = ""
                        focusRequester.requestFocus()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE)),
                enabled = placa.isNotBlank() && tipoTrabajo.isNotBlank() && descripcion.isNotBlank()
            ) {
                Text("GUARDAR REPORTE", fontWeight = FontWeight.Bold)
            }

            // Lista de reportes recientes (opcional, para visualización rápida)
            if (reportesRecientes.isNotEmpty()) {
                Text("Reportes Recientes:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                reportesRecientes.forEach { rep ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${rep.placa} - ${rep.tipoTrabajo}", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(rep.descripcion, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 2)
                            Text("Fecha: ${rep.fechaHora}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorkReportScreenPreview() {
    RegistroTheme { WorkReportScreen() }
}
