package com.example.registro.ui.screens // Paquete donde se encuentra la pantalla

import androidx.compose.foundation.background // Importación para manejar fondos
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.* // Importación para el manejo de layouts (Box, Column, Row, etc.)
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable // Importación para definir funciones Composable
import androidx.compose.ui.Modifier // Importación para usar modificadores de UI
import androidx.compose.ui.graphics.Color // Importación para el manejo de colores
import androidx.compose.ui.tooling.preview.Preview // Importación para la vista previa en el IDE
import com.example.registro.ui.theme.RegistroTheme // Importación del tema de la aplicación

import androidx.compose.material3.* // Importación de componentes de Material Design 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment // Importación para alineación de elementos
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp // Importación para unidades de medida en densidad de píxeles
import androidx.compose.foundation.text.KeyboardOptions // Importación para configurar opciones del teclado
import androidx.compose.ui.text.input.KeyboardType // Importación para definir el tipo de entrada del teclado
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign // Importación para alinear texto

import com.example.registro.ui.UnitViewModel
import com.example.registro.data.TemperatureEntity
import com.example.registro.ui.utils.PrintUtils
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // Anotación para usar APIs experimentales de Material 3
@Composable // Define que esta función es un componente de la interfaz de usuario
fun TemperatureTakingScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // Estado para la barra de búsqueda superior
    var searchQuery by remember { mutableStateOf("") }
    // Estado para almacenar el valor del ID del vehículo o placa
    var vehicleId by remember { mutableStateOf("") }
    // Estado para el Número de Unidad
    var numeroUnidad by remember { mutableStateOf("") }
    // Estado para almacenar el valor de la primera temperatura
    var temp1 by remember { mutableStateOf("") }
    // Estado para almacenar el valor de la segunda temperatura
    var temp2 by remember { mutableStateOf("") }
    // Estado para almacenar los comentarios adicionales
    var comments by remember { mutableStateOf("") }
    // Estado para la unidad de temperatura (C o F)
    var tempUnit by remember { mutableStateOf("C") }

    // Observar mensajes del ViewModel
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val registrosRecientes by (viewModel?.registrosRecientes?.collectAsState() ?: remember { mutableStateOf(emptyList()) })

    var recordToDelete by remember { mutableStateOf<Int?>(null) }

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
            },
            containerColor = Color(0xFFB71C1C), // Rojo para errores
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Mostrar Alerta si el registro fue exitoso
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Éxito") },
            text = { Text(successMessage!!) },
            confirmButton = {
                TextButton(onClick = { viewModel?.clearMessages() }) {
                    Text("Aceptar")
                }
            },
            containerColor = Color(0xFF1B5E20), // Verde para éxito
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Confirmación para eliminar
    if (recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = { Text("¿Eliminar registro?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.eliminarTemperatura(recordToDelete!!)
                        recordToDelete = null
                    }
                ) {
                    Text("ELIMINAR", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("CANCELAR", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFFB71C1C),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    // Lógica de búsqueda automática
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && viewModel != null) {
            val unidadEncontrada = viewModel.buscarUnidad(searchQuery)
            if (unidadEncontrada != null) {
                vehicleId = unidadEncontrada.placa
                numeroUnidad = unidadEncontrada.numeroUnidad
            }
        }
    }

    // Solicitar foco al iniciar la pantalla
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Estructura principal con Scaffold para manejar TopBar y márgenes de sistema
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Toma de Temperatura", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF052A50) // Mismo color azul que el fondo
                )
            )
        },
        containerColor = Color(0xFF052A50) // Fondo de toda la pantalla
    ) { innerPadding ->
        // Contenido Principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplica el margen seguro de la TopBar automáticamente
                .imePadding() // Empuja el contenido hacia arriba cuando sale el teclado
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Permite el scroll del formulario
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra de Búsqueda Superior
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar unidad (Placa o ID)", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = Color.White.copy(alpha = 0.2f)
            )

            // 1. Placa
            OutlinedTextField(
                value = vehicleId,
                onValueChange = { vehicleId = it },
                label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // 2. Numero de Unidad
            OutlinedTextField(
                value = numeroUnidad,
                onValueChange = { numeroUnidad = it },
                label = { Text("Numero de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Selector de Unidad de Temperatura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = tempUnit == "F",
                        onCheckedChange = { if (it) tempUnit = "F" },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF52A8EE),
                            uncheckedColor = Color.White.copy(alpha = 0.6f),
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Fahrenheit (F)", color = Color.White)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = tempUnit == "C",
                        onCheckedChange = { if (it) tempUnit = "C" },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF52A8EE),
                            uncheckedColor = Color.White.copy(alpha = 0.6f),
                            checkmarkColor = Color.White
                        )
                    )
                    Text("Celsius (C)", color = Color.White)
                }
            }

            // Fila para las temperaturas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = temp1,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue == "-" || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            temp1 = newValue
                        }
                    },
                    label = { Text("Temp 1", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                OutlinedTextField(
                    value = temp2,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue == "-" || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            temp2 = newValue
                        }
                    },
                    label = { Text("Temp 2", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )
            }

            // Comentarios
            val maxChar = 80
            OutlinedTextField(
                value = comments,
                onValueChange = { if (it.length <= maxChar) comments = it },
                label = { Text("Comentarios", color = Color.White.copy(alpha = 0.7f)) },
                supportingText = {
                    Text(
                        text = "${comments.length} / $maxChar",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp), // Aumentado para el contador
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón GUARDAR
            Button(
                onClick = {
                    viewModel?.guardarTemperatura(
                        placa = vehicleId,
                        numeroUnidad = numeroUnidad,
                        temp1 = temp1,
                        temp2 = temp2,
                        unidadTemp = tempUnit,
                        comentarios = comments,
                        onSuccess = {
                            // Limpiar campos tras guardar
                            searchQuery = ""; vehicleId = ""; numeroUnidad = ""
                            temp1 = ""; temp2 = ""; comments = ""; tempUnit = "C"
                            // Devolver el foco a la barra de búsqueda
                            focusRequester.requestFocus()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                // Se habilita si Placa, Número de Unidad y Temperaturas están llenos
                enabled = vehicleId.isNotBlank() && numeroUnidad.isNotBlank() && temp1.isNotBlank() && temp2.isNotBlank()
            ) {
                Text(text = "GUARDAR", style = MaterialTheme.typography.titleMedium)
            }

            // Botón IMPRIMIR (Reporte del día)
            Button(
                onClick ={
                    coroutineScope.launch {
                        val registros = viewModel?.obtenerRegistrosDelDia() ?: emptyList()
                        PrintUtils.imprimirReporteDelDia(context, registros)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.55f) // Aumentado de 0.4f a 0.55f para asegurar espacio
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "IMPRIMIR", 
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1 // Evita que el texto intente dividirse en dos líneas
                )
            }

            // LISTA DE REGISTROS RECIENTES
            if (registrosRecientes.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                )
                
                Text(
                    "Últimos registros de hoy:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                registrosRecientes.forEach { reg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${reg.placa} - Unid: ${reg.numeroUnidad}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text("Temps: ${reg.temp1}${reg.unidadTemp} / ${reg.temp2}${reg.unidadTemp}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                            IconButton(onClick = { recordToDelete = reg.id }) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFE57373))
                            }
                        }
                    }
                }
            }
            
            // Margen final de seguridad
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TemperatureTakingScreenPreview() {
    RegistroTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TemperatureTakingScreen(onBackClick = {})
        }
    }
}
