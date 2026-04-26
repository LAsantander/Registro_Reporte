package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.registro.ui.theme.RegistroTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.registro.ui.UnitViewModel

/**
 * Pantalla de Registro de Unidades.
 * Ahora integrada con el ViewModel para persistencia en Room y manejo de alertas de duplicados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistryScreen(
    viewModel: UnitViewModel? = null,
    onBackClick: () -> Unit = {}
) {
    // Estados para los campos de texto
    var id by remember { mutableIntStateOf(0) } // ID de la unidad (0 si es nueva)
    var searchQuery by remember { mutableStateOf("") } // Para buscar unidades existentes
    var placa by remember { mutableStateOf("") }
    var numeroUnidad by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var serie by remember { mutableStateOf("") }

    // Lógica de búsqueda automática para edición
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank() && viewModel != null) {
            val unidadEncontrada = viewModel.buscarUnidad(searchQuery)
            if (unidadEncontrada != null) {
                id = unidadEncontrada.id
                placa = unidadEncontrada.placa
                numeroUnidad = unidadEncontrada.numeroUnidad
                marca = unidadEncontrada.marca
                modelo = unidadEncontrada.modelo
                serie = unidadEncontrada.serie
            }
        }
    }

    // Estados para el menú desplegable
    var expanded by remember { mutableStateOf(false) }
    val opcionesMarcas = listOf("Thermo King", "Carrier")

    // Observar mensajes del ViewModel
    val errorMessage by (viewModel?.errorMessage?.collectAsState() ?: remember { mutableStateOf(null) })
    val successMessage by (viewModel?.successMessage?.collectAsState() ?: remember { mutableStateOf(null) })

    // Mostrar Alerta si hay un error
    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel?.clearMessages() },
            title = { Text("Error de Registro") },
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF052A50)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Botón de retroceso
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 32.dp)
                .padding(top = 48.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registro de Unidad",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Barra de Búsqueda para Editar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar para editar (Placa o ID)", color = Color.White.copy(alpha = 0.7f)) },
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
                value = placa,
                onValueChange = { placa = it },
                label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                )
            )

            // 2. Número de Unidad
            OutlinedTextField(
                value = numeroUnidad,
                onValueChange = { numeroUnidad = it },
                label = { Text("Número de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                )
            )

            // 3. Marca (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Marca de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF052A50))
                ) {
                    opcionesMarcas.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(text = opcion, color = Color.White) },
                            onClick = {
                                marca = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            // 4. Modelo
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                )
            )

            // 5. Serie
            OutlinedTextField(
                value = serie,
                onValueChange = { serie = it },
                label = { Text("Serie de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botón GUARDAR / ACTUALIZAR
            Button(
                onClick = {
                    if (id == 0) {
                        viewModel?.guardarUnidad(
                            placa = placa,
                            numeroUnidad = numeroUnidad,
                            marca = marca,
                            modelo = modelo,
                            serie = serie,
                            onSuccess = {
                                // Limpiar campos tras guardar
                                id = 0; searchQuery = ""; placa = ""; numeroUnidad = ""; marca = ""; modelo = ""; serie = ""
                            }
                        )
                    } else {
                        viewModel?.actualizarUnidad(
                            id = id,
                            placa = placa,
                            numeroUnidad = numeroUnidad,
                            marca = marca,
                            modelo = modelo,
                            serie = serie,
                            onSuccess = {
                                // Limpiar campos tras actualizar
                                id = 0; searchQuery = ""; placa = ""; numeroUnidad = ""; marca = ""; modelo = ""; serie = ""
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (id == 0) "GUARDAR UNIDAD" else "ACTUALIZAR DATOS",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistryScreenPreview() {
    RegistroTheme {
        RegistryScreen()
    }
}
