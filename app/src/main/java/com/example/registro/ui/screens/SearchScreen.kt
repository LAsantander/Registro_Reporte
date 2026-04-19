package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.registro.ui.theme.RegistroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSearchClick: (String) -> Unit
) {
    // Estado para almacenar el texto ingresado en el campo de búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Contenedor principal que ocupa toda la pantalla con fondo azul oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF052A50)),
        contentAlignment = Alignment.TopCenter // Alineado al tope para poder moverlo hacia arriba
    ) {
        // Columna para organizar el campo de texto y el botón verticalmente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 100.dp), // Espacio desde arriba (ajusta este valor para subirlo más)
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de entrada de texto estilizado para el diseño oscuro
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Ingrese placa o ID", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    // Icono de lupa blanco
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                },
                singleLine = true,
                // Personalización de colores para que el campo resalte sobre el fondo azul
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

            // Botón principal de búsqueda
            Button(
                onClick = { onSearchClick(searchQuery) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                // El botón solo se habilita si hay texto escrito
                enabled = searchQuery.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF52A8EE), // Azul brillante
                    contentColor = Color.White,
                    // Colores cuando el botón está desactivado
                    disabledContainerColor = Color(0xFF8DBFF5).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text("BUSCAR")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    RegistroTheme {
        SearchScreen(onSearchClick = {})
    }
}
