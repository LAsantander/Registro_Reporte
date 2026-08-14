package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.ui.UnitViewModel
import com.example.registro.ui.theme.RegistroTheme
import com.example.registro.data.UserSettings

/**
 * Pantalla de Reporte de Trabajo.
 * Permite documentar intervenciones técnicas, mantenimientos y reparaciones.
 * Incluye funciones de autocompletado por placa y edición de reportes existentes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkReportScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    var searchQuery by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var ot by remember { mutableStateOf("") }
    var modeloUnidad by remember { mutableStateOf("") }
    var tipoTrabajo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tecnico by remember { mutableStateOf("") }
    var repuestos by remember { mutableStateOf("") }
    var editingReportId by remember { mutableIntStateOf(0) }

    val reportesRecientes by (viewModel?.reportesTrabajoRecientes?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })

    // Observar configuración del técnico predeterminado
    val userSettings by (viewModel?.userSettings?.collectAsState() ?: remember { mutableStateOf(UserSettings()) })

    // Inicializar el técnico con el valor predeterminado si el campo está vacío y no estamos editando
    LaunchedEffect(userSettings.defaultTechnician, editingReportId) {
        if (editingReportId == 0 && tecnico.isBlank()) {
            tecnico = userSettings.defaultTechnician
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && viewModel != null) {
            val unidadEncontrada = viewModel.buscarUnidad(searchQuery)
            if (unidadEncontrada != null) {
                placa = unidadEncontrada.placa
                modeloUnidad = "${unidadEncontrada.marca} ${unidadEncontrada.modelo}".trim()
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

            // Fila: Placa y OT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = placa, onValueChange = { placa = it },
                    label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
                )
                OutlinedTextField(
                    value = ot, onValueChange = { ot = it },
                    label = { Text("OT", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (userSettings.otOnlyNumbers) KeyboardType.Number else KeyboardType.Text,
                        capitalization = if (userSettings.otOnlyNumbers) KeyboardCapitalization.None else KeyboardCapitalization.Characters
                    ),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
                )
            }

            // Modelo de Unidad debajo
            OutlinedTextField(
                value = modeloUnidad, onValueChange = { modeloUnidad = it },
                label = { Text("Modelo de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

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
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            // Técnico y Repuestos
            OutlinedTextField(
                value = tecnico, onValueChange = { tecnico = it },
                label = { Text("Técnico Asignado", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            OutlinedTextField(
                value = repuestos, onValueChange = { repuestos = it },
                label = { Text("Repuestos / Materiales", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Guardar / Actualizar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (editingReportId == 0) {
                            viewModel?.guardarReporteTrabajo(placa, ot, modeloUnidad, tipoTrabajo, descripcion, tecnico, repuestos) {
                                searchQuery = ""; placa = ""; ot = ""; modeloUnidad = ""; tipoTrabajo = ""; descripcion = ""; repuestos = ""
                                tecnico = userSettings.defaultTechnician // Reset al técnico predeterminado
                                editingReportId = 0
                                focusRequester.requestFocus()
                            }
                        } else {
                            viewModel?.actualizarReporteTrabajo(editingReportId, placa, ot, modeloUnidad, tipoTrabajo, descripcion, tecnico, repuestos) {
                                searchQuery = ""; placa = ""; ot = ""; modeloUnidad = ""; tipoTrabajo = ""; descripcion = ""; repuestos = ""
                                tecnico = userSettings.defaultTechnician // Reset al técnico predeterminado
                                editingReportId = 0
                                focusRequester.requestFocus()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(if (editingReportId == 0) 0.6f else 0.5f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE)),
                    enabled = placa.isNotBlank() && ot.isNotBlank() && tipoTrabajo.isNotBlank() && descripcion.isNotBlank()
                ) {
                    Text(if (editingReportId == 0) "GUARDAR" else "ACTUALIZAR", fontWeight = FontWeight.Bold)
                }

                if (editingReportId != 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = {
                            searchQuery = ""; placa = ""; ot = ""; modeloUnidad = ""; tipoTrabajo = ""; descripcion = ""; repuestos = ""
                            tecnico = userSettings.defaultTechnician // Reset al técnico predeterminado
                            editingReportId = 0
                        },
                        modifier = Modifier.height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                    ) {
                        Text("CANCELAR")
                    }
                }
            }

            // Lista de reportes recientes (opcional, para visualización rápida)
            if (reportesRecientes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Reportes Recientes:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                reportesRecientes.forEach { rep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                editingReportId = rep.id
                                placa = rep.placa
                                ot = rep.ot
                                modeloUnidad = rep.modeloUnidad ?: ""
                                tipoTrabajo = rep.tipoTrabajo
                                descripcion = rep.descripcion
                                tecnico = rep.tecnico
                                repuestos = rep.repuestos
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Box {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${rep.placa} - ${rep.tipoTrabajo}", color = Color.White, fontWeight = FontWeight.Bold)
                                    if (rep.ot.isNotBlank()) {
                                        Text("OT: ${rep.ot}", color = Color(0xFF52A8EE), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(rep.descripcion, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 2)
                                Text("Fecha: ${rep.fechaHora}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            
                            IconButton(
                                onClick = { viewModel?.eliminarReporteTrabajo(rep.id) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE57373).copy(alpha = 0.7f))
                            }
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
