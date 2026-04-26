package com.example.registro.ui.screens // Define el paquete donde se encuentra esta pantalla

import androidx.compose.foundation.background // Importa la capacidad de poner fondos de color
import androidx.compose.foundation.clickable // Importa la capacidad de hacer elementos clickeables
import androidx.compose.foundation.layout.* // Importa herramientas de diseño como Box, Column, Row, etc.
import androidx.compose.foundation.rememberScrollState // Importa el estado para recordar la posición del scroll
import androidx.compose.foundation.verticalScroll // Importa la capacidad de hacer scroll vertical
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // Importa los componentes de Material Design 3
import androidx.compose.runtime.* // Importa las herramientas de manejo de estado (remember, mutableStateOf)
import androidx.compose.ui.Alignment // Importa alineaciones para los elementos
import androidx.compose.ui.Modifier // Importa modificadores para personalizar componentes
import androidx.compose.ui.graphics.Color // Importa la clase para manejar colores
import androidx.compose.ui.text.font.FontWeight // Importa estilos de grosor de fuente
import androidx.compose.ui.tooling.preview.Preview // Importa la capacidad de ver previas en el IDE
import androidx.compose.ui.unit.dp // Importa unidades de medida dp (densidad de píxeles)
import androidx.compose.ui.unit.sp // Importa unidades de medida sp (para texto)
import com.example.registro.ui.theme.RegistroTheme // Importa el tema visual del proyecto

/**
 * Pantalla de Inspección Técnica (Checklist).
 * Permite registrar el estado de diferentes componentes de la unidad mediante selectores de nivel.
 */
@Composable // Indica que esta función es un componente de interfaz de usuario
fun CheckListScreen(
    onBackClick: () -> Unit = {}
) {
    // Variable para recordar la opción seleccionada en "Nivel de aceite"
    var opcionAceite by remember { mutableStateOf("") }
    // Variable para recordar la opción seleccionada en "Refrigerante de motor"
    var opcionRefrigerante by remember { mutableStateOf("") }
    // Variable para recordar la opción seleccionada en "Estado de las correas"
    var opcionCorreas by remember { mutableStateOf("") }
    
    // Lista de opciones generales para las primeras dos cajas (Bajo, Medio, Completo)
    val opciones = listOf("Bajo", "Medio", "Completo")

    // Contenedor principal que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize() // Hace que el contenedor ocupe todo el espacio disponible
            .background(Color(0xFF052A50)), // Aplica el fondo azul oscuro característico
        contentAlignment = Alignment.TopCenter // Alinea el contenido al centro en la parte superior
    ) {
        // Botón de retroceso
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

        // Columna que organiza los elementos uno debajo de otro con scroll
        Column(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho de la pantalla
                .padding(horizontal = 24.dp) // Añade un margen lateral de 24dp
                .padding(top = 48.dp) // Añade un margen superior de 48dp
                .verticalScroll(rememberScrollState()), // Permite que la columna sea deslizable
            horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos de forma horizontal
            verticalArrangement = Arrangement.spacedBy(20.dp) // Añade un espacio de 20dp entre cada elemento
        ) {
            // Texto del título de la pantalla
            Text(
                text = "Inspección Técnica", // Texto que se muestra
                color = Color.White, // Color de texto blanco
                fontSize = 24.sp, // Tamaño de fuente de 24sp
                fontWeight = FontWeight.Bold, // Texto en negrita
                modifier = Modifier.padding(bottom = 8.dp) // Margen inferior de 8dp
            )

            // ---------------------------------------------------------
            // 1. CAJA DE INSPECCIÓN: Nivel de aceite
            // ---------------------------------------------------------
            Surface( // Superficie que actúa como contenedor de la caja
                modifier = Modifier.fillMaxWidth(), // Ancho completo
                color = Color.White.copy(alpha = 0.05f), // Color blanco muy transparente (efecto traslúcido)
                shape = MaterialTheme.shapes.medium // Bordes redondeados de tamaño medio
            ) {
                Column( // Columna interna de la caja
                    modifier = Modifier.padding(vertical = 20.dp), // Margen interno vertical de 20dp
                    horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido internamente
                ) {
                    Text( // Título de la caja
                        text = "Nivel de aceite", // Nombre del ítem a inspeccionar
                        color = Color.White, // Color blanco
                        fontSize = 20.sp, // Tamaño de 20sp
                        fontWeight = FontWeight.Bold, // Negrita
                        modifier = Modifier.padding(bottom = 16.dp) // Margen inferior de 16dp
                    )

                    Row( // Fila para mostrar las opciones horizontalmente
                        modifier = Modifier.fillMaxWidth(), // Ancho completo de la fila
                        horizontalArrangement = Arrangement.SpaceEvenly, // Distribuye las opciones equitativamente
                        verticalAlignment = Alignment.CenterVertically // Centra verticalmente los elementos de la fila
                    ) {
                        opciones.forEach { opcion -> // Itera sobre cada opción (Bajo, Medio, Completo)
                            Column( // Columna para cada par Checkbox + Texto
                                horizontalAlignment = Alignment.CenterHorizontally, // Centra elementos
                                modifier = Modifier
                                    .clickable { opcionAceite = opcion } // Permite seleccionar al hacer click en el área
                                    .padding(horizontal = 8.dp) // Margen lateral de 8dp
                            ) {
                                Checkbox( // El control de selección
                                    checked = (opcionAceite == opcion), // Está marcado si coincide con el estado
                                    onCheckedChange = { if (it) opcionAceite = opcion }, // Cambia el estado al marcar
                                    colors = CheckboxDefaults.colors( // Personaliza los colores del Checkbox
                                        checkedColor = Color(0xFF52A8EE), // Color azul brillante cuando está marcado
                                        uncheckedColor = Color.White.copy(alpha = 0.6f), // Color tenue cuando no está marcado
                                        checkmarkColor = Color.White // Color del check interno
                                    )
                                )
                                Text( // Etiqueta de la opción
                                    text = opcion, // "Bajo", "Medio" o "Completo"
                                    color = Color.White, // Color blanco
                                    fontSize = 14.sp, // Tamaño de 14sp
                                    fontWeight = if (opcionAceite == opcion) FontWeight.Bold else FontWeight.Normal // Resalta si está seleccionado
                                )
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // 2. CAJA DE INSPECCIÓN: Refrigerante de motor
            // ---------------------------------------------------------
            Surface( // Superficie contenedora de la segunda caja
                modifier = Modifier.fillMaxWidth(), // Ancho completo
                color = Color.White.copy(alpha = 0.05f), // Fondo traslúcido
                shape = MaterialTheme.shapes.medium // Bordes redondeados
            ) {
                Column( // Contenido vertical de la caja
                    modifier = Modifier.padding(vertical = 20.dp), // Espaciado interno
                    horizontalAlignment = Alignment.CenterHorizontally // Centrado
                ) {
                    Text( // Nombre del ítem
                        text = "Refrigerante de motor", // Texto a mostrar
                        color = Color.White, // Color blanco
                        fontSize = 20.sp, // Tamaño de fuente
                        fontWeight = FontWeight.Bold, // Negrita
                        modifier = Modifier.padding(bottom = 16.dp) // Margen inferior
                    )

                    Row( // Fila horizontal para opciones
                        modifier = Modifier.fillMaxWidth(), // Ancho completo
                        horizontalArrangement = Arrangement.SpaceEvenly, // Espaciado igual
                        verticalAlignment = Alignment.CenterVertically // Centrado vertical
                    ) {
                        opciones.forEach { opcion -> // Repite por cada opción
                            Column( // Contenedor vertical de cada opción
                                horizontalAlignment = Alignment.CenterHorizontally, // Centrado
                                modifier = Modifier
                                    .clickable { opcionRefrigerante = opcion } // Click para seleccionar
                                    .padding(horizontal = 8.dp) // Margen lateral
                            ) {
                                Checkbox( // Control de selección
                                    checked = (opcionRefrigerante == opcion), // Estado de selección
                                    onCheckedChange = { if (it) opcionRefrigerante = opcion }, // Acción al cambiar
                                    colors = CheckboxDefaults.colors( // Personalización de colores
                                        checkedColor = Color(0xFF52A8EE), // Azul brillante
                                        uncheckedColor = Color.White.copy(alpha = 0.6f), // Blanco tenue
                                        checkmarkColor = Color.White // Check blanco
                                    )
                                )
                                Text( // Nombre de la opción
                                    text = opcion, // Texto de nivel
                                    color = Color.White, // Blanco
                                    fontSize = 14.sp, // Tamaño
                                    fontWeight = if (opcionRefrigerante == opcion) FontWeight.Bold else FontWeight.Normal // Negrita si está activa
                                )
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // 3. CAJA DE INSPECCIÓN: Estado de las correas
            // ---------------------------------------------------------
            Surface( // Superficie para la tercera caja
                modifier = Modifier.fillMaxWidth(), // Ancho completo
                color = Color.White.copy(alpha = 0.05f), // Fondo traslúcido
                shape = MaterialTheme.shapes.medium // Bordes redondeados
            ) {
                Column( // Contenedor vertical interno
                    modifier = Modifier.padding(vertical = 20.dp), // Espaciado vertical
                    horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
                ) {
                    Text( // Título de esta inspección
                        text = "Estado de las correas", // Texto descriptivo
                        color = Color.White, // Color blanco
                        fontSize = 20.sp, // Tamaño de fuente
                        fontWeight = FontWeight.Bold, // Negrita
                        modifier = Modifier.padding(bottom = 16.dp) // Espacio inferior
                    )

                    Row( // Fila para las opciones específicas de correas
                        modifier = Modifier.fillMaxWidth(), // Ancho completo
                        horizontalArrangement = Arrangement.SpaceEvenly, // Distribución uniforme
                        verticalAlignment = Alignment.CenterVertically // Alineación vertical
                    ) {
                        // Lista de opciones personalizadas para este punto
                        val opcionesCorreasList = listOf("Buenas", "Regular", "Desgastadas")
                        opcionesCorreasList.forEach { opcion -> // Itera sobre cada estado
                            Column( // Contenedor de cada estado (Checkbox + Texto)
                                horizontalAlignment = Alignment.CenterHorizontally, // Centrado
                                modifier = Modifier
                                    .clickable { opcionCorreas = opcion } // Acción de click en el área
                                    .padding(horizontal = 8.dp) // Margen lateral
                            ) {
                                Checkbox( // El control visual de selección
                                    checked = (opcionCorreas == opcion), // Marcado si coincide con el estado
                                    onCheckedChange = { if (it) opcionCorreas = opcion }, // Actualiza selección
                                    colors = CheckboxDefaults.colors( // Colores personalizados
                                        checkedColor = Color(0xFF52A8EE), // Azul si está marcado
                                        uncheckedColor = Color.White.copy(alpha = 0.6f), // Grisáceo si no
                                        checkmarkColor = Color.White // Check blanco
                                    )
                                )
                                Text( // Etiqueta del estado
                                    text = opcion, // Texto "Buenas", "Regular", etc.
                                    color = Color.White, // Color blanco
                                    fontSize = 14.sp, // Tamaño de fuente
                                    fontWeight = if (opcionCorreas == opcion) FontWeight.Bold else FontWeight.Normal // Resalte visual
                                )
                            }
                        }
                    }
                }
            }

            // Añade un espacio de 20dp antes del botón
            Spacer(modifier = Modifier.height(20.dp))

            // Botón principal para procesar y guardar los datos
            Button(
                onClick = { /* Aquí iría la lógica para enviar o guardar los datos */ }, // Acción al pulsar
                modifier = Modifier
                    .fillMaxWidth(0.7f) // El botón ocupa el 70% del ancho disponible
                    .height(56.dp), // Altura estándar de 56dp
                colors = ButtonDefaults.buttonColors( // Colores del botón
                    containerColor = Color(0xFF52A8EE), // Fondo azul brillante
                    contentColor = Color.White // Texto blanco
                )
            ) {
                // Texto dentro del botón
                Text(text = "GUARDAR INSPECCIÓN", fontWeight = FontWeight.Bold) // Texto en negrita
            }

            // Espacio al final de la columna para que el scroll no corte el contenido
            Spacer(modifier = Modifier.height(40.dp)) // Margen inferior de 40dp
        }
    }
}

/**
 * Función que permite visualizar la pantalla en el panel de diseño de Android Studio.
 */
@Preview(showBackground = true) // Muestra el fondo en la previa
@Composable // Es un componente visual
fun CheckListScreenPreview() { 
    RegistroTheme { // Envuelve la pantalla en el tema del proyecto
        CheckListScreen() // Llama a la pantalla principal
    }
}
