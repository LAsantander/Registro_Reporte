package com.example.registro.ui.screens // Paquete donde se encuentra la pantalla

import androidx.compose.foundation.background // Importación para manejar fondos
import androidx.compose.foundation.layout.* // Importación para el manejo de layouts (Box, Column, Row, etc.)
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable // Importación para definir funciones Composable
import androidx.compose.ui.Modifier // Importación para usar modificadores de UI
import androidx.compose.ui.graphics.Color // Importación para el manejo de colores
import androidx.compose.ui.tooling.preview.Preview // Importación para la vista previa en el IDE
import com.example.registro.ui.theme.RegistroTheme // Importación del tema de la aplicación

import androidx.compose.material3.* // Importación de componentes de Material Design 3
import androidx.compose.runtime.* // Importación de funciones de estado (remember, mutableStateOf)
import androidx.compose.ui.Alignment // Importación para alineación de elementos
import androidx.compose.ui.unit.dp // Importación para unidades de medida en densidad de píles
import androidx.compose.ui.text.font.FontWeight // Importación para estilos de fuente
import androidx.compose.ui.unit.sp // Importación para unidades de medida de texto

@OptIn(ExperimentalMaterial3Api::class) // Anotación para usar APIs experimentales de Material 3
@Composable // Define que esta función es un componente de la interfaz de usuario
fun RegistryScreen() { // Función principal de la pantalla de registro de unidad
    // Estados para almacenar el valor de cada uno de los 5 campos de texto
    var placa by remember { mutableStateOf("") } // Estado para Placa o Número
    var marca by remember { mutableStateOf("") } // Estado para Marca de unidad
    var modelo by remember { mutableStateOf("") } // Estado para Modelo de unidad
    var serie by remember { mutableStateOf("") } // Estado para Serie de unidad
    var empresa by remember { mutableStateOf("") } // Estado para Empresa o Propietario

    // Estados para controlar el menú desplegable de "Marca de Unidad"
    var expanded by remember { mutableStateOf(false) } // Controla si el menú está abierto o cerrado
    val opcionesMarcas = listOf("Thermo King", "Carrier") // Lista de marcas disponibles solicitadas

    // Contenedor principal de tipo Box para manejar el fondo azul oscuro
    Box(
        modifier = Modifier // Aplicamos modificadores al Box
            .fillMaxSize() // El Box ocupará todo el tamaño disponible de la pantalla
            .background(Color(0xFF052A50)), // Aplicamos el color de fondo azul oscuro característico
        contentAlignment = Alignment.TopCenter // Alineamos el contenido al tope superior
    ) {
        // Columna para organizar los 5 campos de texto y el botón verticalmente
        Column(
            modifier = Modifier // Aplicamos modificadores a la columna
                .fillMaxWidth() // La columna ocupará todo el ancho disponible
                .padding(horizontal = 32.dp) // Margen lateral de 32dp
                .padding(top = 40.dp) // Espacio desde la parte superior
                .verticalScroll(rememberScrollState()), // Habilitamos scroll para que quepan todos los campos
            verticalArrangement = Arrangement.spacedBy(16.dp), // Separación de 16dp entre cada elemento
            horizontalAlignment = Alignment.CenterHorizontally // Centramos los elementos horizontalmente
        ) {
            // Título de la pantalla de registro
            Text(
                text = "Registro de Unidad", // Texto del título
                color = Color.White, // Color blanco para contraste
                fontSize = 24.sp, // Tamaño de letra grande
                fontWeight = FontWeight.Bold, // Texto en negrita
                modifier = Modifier.padding(bottom = 8.dp) // Espacio adicional debajo del título
            )

            // 1. Campo de texto para Placa o Número
            OutlinedTextField(
                value = placa, // Valor vinculado al estado
                onValueChange = { placa = it }, // Actualización del estado
                label = { Text("Placa", color = Color.White.copy(alpha = 0.7f)) }, // Etiqueta blanca suave
                modifier = Modifier.fillMaxWidth(), // Ancho completo
                singleLine = true, // Una sola línea
                colors = OutlinedTextFieldDefaults.colors( // Estilo coherente con la app
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // 2. Campo de texto para Empresa / Propietario (MOVIDO AQUÍ: Debajo de Placa)
            OutlinedTextField(
                value = empresa,
                onValueChange = { empresa = it },
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

            // 3. Campo de lista (Dropdown) para Marca de unidad
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }, // Control de apertura/cierre
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = marca, // Muestra la marca seleccionada
                    onValueChange = {}, // No permite escritura manual
                    readOnly = true, // Obliga a elegir de la lista
                    label = { Text("Marca de Unidad", color = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, // Flecha indicadora
                    modifier = Modifier
                        .menuAnchor() // Anclaje para el menú desplegable
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                // Menú desplegable con las marcas solicitadas
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }, // Cierra al pulsar fuera
                    modifier = Modifier.background(Color(0xFF052A50)) // Fondo a juego con la pantalla
                ) {
                    opcionesMarcas.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(text = opcion, color = Color.White) }, // Texto de la marca
                            onClick = {
                                marca = opcion // Selecciona la marca
                                expanded = false // Cierra el menú
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // 4. Campo de texto para Modelo de unidad
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
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // 5. Campo de texto para Serie de unidad
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
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Espacio antes del botón de guardado
            Spacer(modifier = Modifier.height(14.dp))

            // Botón para Guardar el registro completo de la unidad
            Button(
                onClick = { /* Lógica de guardado */ },
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Ancho al 70% como en las otras pantallas
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE), // Azul brillante
                    contentColor = Color.White
                )
            ) {
                Text(text = "GUARDAR UNIDAD", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true) // Habilita la vista previa en el IDE
@Composable
fun RegistryScreenPreview() {
    RegistroTheme {
        RegistryScreen()
    }
}
