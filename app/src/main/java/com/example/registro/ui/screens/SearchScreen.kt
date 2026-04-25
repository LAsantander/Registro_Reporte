package com.example.registro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registro.ui.theme.RegistroTheme

@Composable
fun SearchScreen(
    onNavigateToTemperature: () -> Unit,
    onNavigateToRegistry: () -> Unit,
    onNavigateToChecklist: () -> Unit
) {
    // Contenedor principal que ocupa toda la pantalla con fondo azul oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF052A50)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Columna para organizar los botones verticalmente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menú Principal",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Botones de navegación
            Button(
                onClick = onNavigateToTemperature,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("TOMA DE TEMPERATURA", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToRegistry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("REGISTRO DE UNIDAD", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToChecklist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF52A8EE))
            ) {
                Text("INSPECCIÓN TÉCNICA", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    RegistroTheme {
        SearchScreen(
            onNavigateToTemperature = {},
            onNavigateToRegistry = {},
            onNavigateToChecklist = {}
        )
    }
}
