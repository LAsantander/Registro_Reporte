package com.example.registro.ui.screens // Paquete donde se encuentra la pantalla

import androidx.compose.foundation.background // Importación para manejar fondos
import androidx.compose.foundation.layout.* // Importación para el manejo de layouts (Box, Column, Row, etc.)
import androidx.compose.runtime.Composable // Importación para definir funciones Composable
import androidx.compose.ui.Modifier // Importación para usar modificadores de UI
import androidx.compose.ui.graphics.Color // Importación para el manejo de colores
import androidx.compose.ui.tooling.preview.Preview // Importación para la vista previa en el IDE
import com.example.registro.ui.theme.RegistroTheme // Importación del tema de la aplicación

import androidx.compose.material3.* // Importación de componentes de Material Design 3
import androidx.compose.runtime.* // Importación de funciones de estado (remember, mutableStateOf)
import androidx.compose.ui.Alignment // Importación para alineación de elementos
import androidx.compose.ui.unit.dp // Importación para unidades de medida en densidad de píxeles
import androidx.compose.foundation.text.KeyboardOptions // Importación para configurar opciones del teclado
import androidx.compose.ui.text.input.KeyboardType // Importación para definir el tipo de entrada del teclado

@OptIn(ExperimentalMaterial3Api::class) // Anotación para usar APIs experimentales de Material 3
@Composable // Define que esta función es un componente de la interfaz de usuario
fun TemperatureTakingScreen() { // Función principal de la pantalla de toma de temperatura
    // Estado para almacenar el valor del ID del vehículo o placa
    var vehicleId by remember { mutableStateOf("") }
    // Estado para almacenar el valor de la primera temperatura
    var temp1 by remember { mutableStateOf("") }
    // Estado para almacenar el valor de la segunda temperatura
    var temp2 by remember { mutableStateOf("") }
    // Estado para almacenar los comentarios adicionales
    var comments by remember { mutableStateOf("") }

    // Contenedor principal de tipo Box para manejar el fondo y la alineación superior
    Box(
        modifier = Modifier // Aplicamos modificadores al Box
            .fillMaxSize() // El Box ocupará todo el tamaño disponible de la pantalla
            .background(Color(0xFF052A50)), // Aplicamos el color de fondo azul oscuro
        contentAlignment = Alignment.TopCenter // Alineamos al tope superior para poder posicionar los elementos más arriba
    ) {
        // Columna para organizar los elementos de entrada uno debajo del otro
        Column(
            modifier = Modifier // Aplicamos modificadores a la columna
                .fillMaxWidth() // La columna ocupará todo el ancho disponible
                .padding(horizontal = 32.dp) // Aplicamos un margen lateral de 32dp
                .padding(top = 100.dp), // Aplicamos un espacio superior de 100dp para subir el contenido
            verticalArrangement = Arrangement.spacedBy(24.dp), // Aumentamos la separación a 24dp entre cada elemento
            horizontalAlignment = Alignment.CenterHorizontally // Centramos los elementos horizontalmente
        ) {
            // Campo de texto para ingresar la placa o el número del vehículo
            OutlinedTextField(
                value = vehicleId, // El valor actual del campo
                onValueChange = { vehicleId = it }, // Actualizamos el estado cuando el usuario escribe
                label = { Text("Placa o número de vehículo", color = Color.White.copy(alpha = 0.7f)) }, // Etiqueta flotante
                modifier = Modifier.fillMaxWidth(), // El campo ocupa todo el ancho de la columna
                singleLine = true, // Evitamos que el texto salte a múltiples líneas
                colors = OutlinedTextFieldDefaults.colors( // Personalización de colores para el campo
                    focusedTextColor = Color.White, // Color del texto cuando tiene el foco
                    unfocusedTextColor = Color.White, // Color del texto cuando no tiene el foco
                    focusedBorderColor = Color.White, // Color del borde cuando tiene el foco
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f), // Color del borde cuando no tiene el foco
                    cursorColor = Color.White, // Color del cursor de escritura
                    focusedLabelColor = Color.White, // Color de la etiqueta cuando tiene el foco
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f) // Color de la etiqueta cuando no tiene el foco
                )
            )

            // Fila para organizar las dos temperaturas una al lado de la otra
            Row(
                modifier = Modifier.fillMaxWidth(), // La fila ocupa todo el ancho disponible
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio de 16dp entre los dos campos
            ) {
                // Campo de texto para la primera temperatura con validación numérica
                OutlinedTextField(
                    value = temp1, // El valor actual de la temperatura 1
                    onValueChange = { newValue -> // Lógica de cambio de valor
                        // Filtro: permite solo números, un punto decimal y un signo menos opcional al inicio
                        if (newValue.isEmpty() || newValue == "-" || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            temp1 = newValue // Actualizamos el estado solo si cumple el formato numérico
                        }
                    },
                    label = { Text("Temp 1", color = Color.White.copy(alpha = 0.7f)) }, // Etiqueta corta para que quepa bien
                    modifier = Modifier.weight(1f), // Usamos weight(1f) para que ocupe la mitad del ancho de la fila
                    singleLine = true, // Una sola línea para evitar saltos
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // Muestra el teclado decimal
                    colors = OutlinedTextFieldDefaults.colors( // Aplicamos los mismos colores que el campo anterior
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    )
                )

                // Campo de texto para la segunda temperatura con la misma lógica
                OutlinedTextField(
                    value = temp2, // El valor actual de la temperatura 2
                    onValueChange = { newValue -> // Lógica de validación
                        // Filtro: permite números positivos, negativos y decimales
                        if (newValue.isEmpty() || newValue == "-" || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                            temp2 = newValue // Asignamos el nuevo valor validado
                        }
                    },
                    label = { Text("Temp 2", color = Color.White.copy(alpha = 0.7f)) }, // Etiqueta corta para coherencia
                    modifier = Modifier.weight(1f), // Usamos weight(1f) para que ocupe la otra mitad del ancho
                    singleLine = true, // Texto en una línea
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // Teclado para decimales y signos
                    colors = OutlinedTextFieldDefaults.colors( // Colores coherentes con el diseño azul
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

            // Campo de texto para comentarios adicionales (más alto para permitir varias líneas)
            OutlinedTextField(
                value = comments, // El valor actual de los comentarios
                onValueChange = { comments = it }, // Actualizamos el estado cuando el usuario escribe
                label = { Text("Comentarios", color = Color.White.copy(alpha = 0.7f)) }, // Etiqueta flotante
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho
                    .height(120.dp), // Altura mayor para que parezca una caja de comentarios
                singleLine = false, // Permitimos múltiples líneas de texto
                maxLines = 4, // Limitamos visualmente a 4 líneas
                colors = OutlinedTextFieldDefaults.colors( // Mismos colores para mantener la consistencia
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                )
            )

            // Espacio adicional específico para separar el botón del campo de comentarios
            Spacer(modifier = Modifier.height(18.dp))

            // Botón para guardar el registro de temperatura
            Button(
                onClick = { /* Aquí irá la lógica para guardar los datos */ }, // Acción al hacer clic
                modifier = Modifier // Aplicamos modificadores al botón
                    .fillMaxWidth(0.7f) // Reducimos el ancho al 70% para que no toque los bordes y se vea más estilizado
                    .height(56.dp), // Altura estándar de 56dp para facilitar el toque
                colors = ButtonDefaults.buttonColors( // Personalización de colores del botón
                    containerColor = Color(0xFF52A8EE), // Color azul brillante para resaltar la acción
                    contentColor = Color.White, // Color del texto en blanco
                    disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f), // Color cuando está desactivado
                    disabledContentColor = Color.White.copy(alpha = 0.5f) // Color del texto cuando está desactivado
                ),
                // El botón solo se habilita si se ha ingresado la placa y ambas temperaturas
                enabled = vehicleId.isNotBlank() && temp1.isNotBlank() && temp2.isNotBlank()
            ) {
                // Texto que se muestra dentro del botón
                Text(
                    text = "GUARDAR REGISTRO", // Mensaje del botón
                    style = MaterialTheme.typography.titleMedium // Estilo de texto mediano
                )
            }

            // Espacio entre el botón de guardar y el de imprimir
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para imprimir el registro
            Button(
                onClick ={ /*aqui ira la logica ara guardar los datos */ }, //Accion al hacer clic
                modifier = Modifier // Aplicamos modificadores al botón
                    .fillMaxWidth(0.38f)
                    .height(46.dp), // Altura estándar de 56dp para facilitar el toque
                colors = ButtonDefaults.buttonColors( // personalizaciones de los colores
                    containerColor = Color(0xFF52A8EE),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF52A8EE).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)

                ),
                enabled = vehicleId.isNotBlank() && temp1.isNotBlank() && temp2.isNotBlank()

            ) {
                Text(
                    text = "IMPRIMIR",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true) // Habilita la vista previa en el panel lateral de Android Studio
@Composable // Define que esta función es para previsualización
fun TemperatureTakingScreenPreview() { // Función para ver cómo queda el diseño sin ejecutar la app
    RegistroTheme { // Aplicamos el tema a la vista previa
        TemperatureTakingScreen() // Llamamos a la pantalla de toma de temperatura
    }
}
