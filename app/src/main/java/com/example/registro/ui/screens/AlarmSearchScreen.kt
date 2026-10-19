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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Build
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

/**
 * Pantalla para la búsqueda y gestión de alarmas por modelo de unidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSearchScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    // Observar modelos de unidad disponibles desde la base de datos
    val dbModelos by if (viewModel != null) {
        viewModel.alarmModels.collectAsState()
    } else {
        remember { mutableStateOf(emptyList()) }
    }
    
    val modelList = remember(dbModelos) {
        val defaultModels = listOf("Thermo King", "Carrier", "T-Series", "Vector", "Supra")
        (defaultModels + dbModelos).distinct().sorted()
    }

    var selectedModelo by remember { mutableStateOf(modelList.firstOrNull() ?: "Thermo King") }
    var codigoAlarmaInput by remember { mutableStateOf("") }
    var customModeloInput by remember { mutableStateOf("") }
    var isCustomModelo by remember { mutableStateOf(false) }

    // Observar resultado de búsqueda
    val foundAlarm by if (viewModel != null) {
        viewModel.foundAlarm.collectAsState()
    } else {
        remember { mutableStateOf(null) }
    }
    val hasSearched by if (viewModel != null) {
        viewModel.hasSearchedAlarm.collectAsState()
    } else {
        remember { mutableStateOf(false) }
    }

    // Observar alertas
    val errorMessage by if (viewModel != null) {
        viewModel.errorMessage.collectAsState()
    } else {
        remember { mutableStateOf(null) }
    }
    val successMessage by if (viewModel != null) {
        viewModel.successMessage.collectAsState()
    } else {
        remember { mutableStateOf(null) }
    }

    // Diálogo para agregar nueva alarma
    var showAddDialog by remember { mutableStateOf(false) }

    // Lista de alarmas del modelo seleccionado
    val modeloEfectivo = if (isCustomModelo) customModeloInput else selectedModelo
    val alarmasDelModelo by if (viewModel != null) {
        viewModel.obtenerAlarmasPorModelo(modeloEfectivo).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    // Manejo de mensajes
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
                title = { Text("Búsqueda de Alarmas", color = Color.White, fontWeight = FontWeight.Bold) },
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
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(44.dp)
                            .background(Color(0xFF52A8EE).copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Add, "Agregar Alarma", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF052A50))
            )
        },
        containerColor = Color(0xFF052A50)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Selector de Modelo de Unidad
            Text(
                text = "1. Selecciona el Modelo de Unidad",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            var dropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = if (isCustomModelo) "Otro modelo (Personalizado)" else selectedModelo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Modelo de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF52A8EE),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF0A3A6B))
                ) {
                    modelList.forEach { modelo ->
                        DropdownMenuItem(
                            text = { Text(modelo, color = Color.White) },
                            onClick = {
                                selectedModelo = modelo
                                isCustomModelo = false
                                dropdownExpanded = false
                                viewModel?.resetAlarmSearch()
                            }
                        )
                    }
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    DropdownMenuItem(
                        text = { Text("+ Escribir otro modelo...", color = Color(0xFF52A8EE), fontWeight = FontWeight.Bold) },
                        onClick = {
                            isCustomModelo = true
                            dropdownExpanded = false
                            viewModel?.resetAlarmSearch()
                        }
                    )
                }
            }

            // Campo para modelo personalizado si seleccionó "+ Escribir otro modelo"
            if (isCustomModelo) {
                OutlinedTextField(
                    value = customModeloInput,
                    onValueChange = { customModeloInput = it; viewModel?.resetAlarmSearch() },
                    label = { Text("Escribe el Modelo de la Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF52A8EE),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }

            // Campo de número de alarma
            Text(
                text = "2. Número o Código de Alarma",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = codigoAlarmaInput,
                onValueChange = { codigoAlarmaInput = it },
                label = { Text("Ejemplo: 10, 12, A01, 63", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                leadingIcon = { Icon(Icons.Default.Warning, null, tint = Color(0xFF52A8EE)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF52A8EE),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Botón Buscar
            Button(
                onClick = {
                    viewModel?.buscarAlarma(modeloEfectivo, codigoAlarmaInput)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE)),
                shape = RoundedCornerShape(12.dp),
                enabled = modeloEfectivo.isNotBlank() && codigoAlarmaInput.isNotBlank()
            ) {
                Icon(Icons.Default.Search, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("BUSCAR ALARMA", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

            // Resultado de la búsqueda
            if (hasSearched) {
                if (foundAlarm != null) {
                    val alarm = foundAlarm!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4A85)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF52A8EE))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF66BB6A),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Alarma ${alarm.codigoAlarma}",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    color = Color(0xFF52A8EE).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = alarm.modelo,
                                        color = Color(0xFF52A8EE),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                            // Descripción
                            Column {
                                Text(
                                    text = "Descripción del Problema:",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = alarm.descripcion,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            // Solución / Acción recomendada
                            if (alarm.solucion.isNotBlank()) {
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Build, null, tint = Color(0xFFFFCA28), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Solución / Acción Recomendada:",
                                            color = Color(0xFFFFCA28),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = alarm.solucion,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // No encontrada
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723).copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF7043))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFF7043), modifier = Modifier.size(36.dp))
                            Text(
                                text = "Alarma No Encontrada",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "No se encontró la alarma '$codigoAlarmaInput' registrada para el modelo '$modeloEfectivo'.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = { showAddDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF52A8EE))
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("REGISTRAR ESTA ALARMA EN LA BD")
                            }
                        }
                    }
                }
            }

            // Sección: Lista de Alarmas Registradas para el Modelo
            if (alarmasDelModelo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Alarmas de $modeloEfectivo (${alarmasDelModelo.size}):",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    TextButton(onClick = { showAddDialog = true }) {
                        Text("+ Añadir Alarma", color = Color(0xFF52A8EE), fontSize = 12.sp)
                    }
                }

                alarmasDelModelo.forEach { alarm ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                codigoAlarmaInput = alarm.codigoAlarma
                                viewModel?.buscarAlarma(modeloEfectivo, alarm.codigoAlarma)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Código ${alarm.codigoAlarma}",
                                        color = Color(0xFF52A8EE),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = alarm.modelo,
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = alarm.descripcion,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                if (alarm.solucion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Solución: ${alarm.solucion}",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel?.eliminarAlarma(alarm.id) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE57373).copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Diálogo para Registrar / Guardar una Nueva Alarma
    if (showAddDialog) {
        var newModelo by remember { mutableStateOf(modeloEfectivo) }
        var newCodigo by remember { mutableStateOf(codigoAlarmaInput) }
        var newDescripcion by remember { mutableStateOf("") }
        var newSolucion by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Registrar Nueva Alarma", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newModelo,
                        onValueChange = { newModelo = it },
                        label = { Text("Modelo de Unidad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    OutlinedTextField(
                        value = newCodigo,
                        onValueChange = { newCodigo = it },
                        label = { Text("Número / Código de Alarma") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                    )
                    OutlinedTextField(
                        value = newDescripcion,
                        onValueChange = { newDescripcion = it },
                        label = { Text("Descripción de la Alarma") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    OutlinedTextField(
                        value = newSolucion,
                        onValueChange = { newSolucion = it },
                        label = { Text("Solución o Acción Recomendada (Opcional)") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.guardarAlarma(newModelo, newCodigo, newDescripcion, newSolucion) {
                            showAddDialog = false
                            if (newModelo.equals(modeloEfectivo, ignoreCase = true)) {
                                codigoAlarmaInput = newCodigo
                                viewModel.buscarAlarma(newModelo, newCodigo)
                            }
                        }
                    },
                    enabled = newModelo.isNotBlank() && newCodigo.isNotBlank() && newDescripcion.isNotBlank()
                ) {
                    Text("GUARDAR ALARMA")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("CANCELAR")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmSearchScreenPreview() {
    RegistroTheme {
        AlarmSearchScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmSearchResultCardPreview() {
    RegistroTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF052A50))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4A85)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF52A8EE))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF66BB6A),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Alarma 10",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            color = Color(0xFF52A8EE).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Thermo King",
                                color = Color(0xFF52A8EE),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Column {
                        Text(
                            text = "Descripción del Problema:",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Alta temperatura de refrigerante del motor diésel.",
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, null, tint = Color(0xFFFFCA28), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Solución / Acción Recomendada:",
                                color = Color(0xFFFFCA28),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verificar nivel de refrigerante, tensión de banda y limpiar serpentín del radiador.",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
