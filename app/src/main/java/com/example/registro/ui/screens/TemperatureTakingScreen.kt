package com.example.registro.ui.screens // Paquete donde se encuentra la pantalla

import androidx.compose.foundation.background // Importación para manejar fondos
import androidx.compose.foundation.layout.* // Importación para el manejo de layouts (Box, Column, Row, etc.)
import androidx.compose.runtime.Composable // Importación para definir funciones Composable
import androidx.compose.ui.Modifier // Importación para usar modificadores de UI
import androidx.compose.ui.graphics.Color // Importación para el manejo de colores
import androidx.compose.ui.tooling.preview.Preview // Importación para la vista previa en el IDE
import com.example.registro.ui.theme.RegistroTheme // Importación del tema de la aplicación

import androidx.compose.material3.* // Importación de componentes de Material Design 3
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment // Importación para alineación de elementos
import androidx.compose.ui.unit.dp // Importación para unidades de medida en densidad de píxeles
import androidx.compose.foundation.text.KeyboardOptions // Importación para configurar opciones del teclado
import androidx.compose.ui.text.input.KeyboardType // Importación para definir el tipo de entrada del teclado
import androidx.compose.ui.text.style.TextAlign // Importación para alinear texto

import com.example.registro.ui.UnitViewModel
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

    // Observar mensajes del ViewModel
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

    // Contenedor principal de tipo Box para manejar el fondo y la alineación superior
    Box(
        modifier = Modifier // Aplicamos modificadores al Box
            .fillMaxSize() // El Box ocupará todo el tamaño disponible de la pantalla
            .background(Color(0xFF052A50)), // Aplicamos el color de fondo azul oscuro
        contentAlignment = Alignment.TopCenter // Alineamos al tope superior para poder posicionar los elementos más arriba
    ) {
        // Botón de retroceso en la parte superior izquierda
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }

        // Columna para organizar los elementos de entrada uno debajo del otro
        Column(
            modifier = Modifier // Aplicamos modificadores a la columna
                .fillMaxWidth() // La columna ocupará todo el ancho disponible
                .padding(horizontal = 32.dp) // Aplicamos un margen lateral de 32dp
                .padding(top = 64.dp), // Aumentado para dar espacio a la flecha de retroceso
            verticalArrangement = Arrangement.spacedBy(16.dp), // Reducido un poco para que quepan todos los campos
            horizontalAlignment = Alignment.CenterHorizontally // Centramos los elementos horizontalmente
        ) {
            // Barra de Búsqueda Superior
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar unidad (Placa o ID)", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
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
            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Comentarios", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false,
                maxLines = 3,
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
                        comentarios = comments,
                        onSuccess = {
                            // Limpiar campos tras guardar
                            searchQuery = ""; vehicleId = ""; numeroUnidad = ""
                            temp1 = ""; temp2 = ""; comments = ""
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
                modifier = Modifier.fillMaxWidth(0.4f).height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text(text = "IMPRIMIR", style = MaterialTheme.typography.titleMedium)
            }
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
