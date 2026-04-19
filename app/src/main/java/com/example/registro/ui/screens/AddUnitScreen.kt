package com.example.registro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.tooling.preview.Preview
import com.example.registro.ui.theme.RegistroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUnitScreen(
    onNavigateBack: () -> Unit,
    onSaveUnit: (plate: String, temp: String) -> Unit
) {
    var plateNumber by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Unidad") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it },
                label = { Text("Número de Placa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = temperature,
                onValueChange = { temperature = it },
                label = { Text("Temperatura Inicial (°C)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSaveUnit(plateNumber, temperature) },
                modifier = Modifier.fillMaxWidth(),
                enabled = plateNumber.isNotBlank() && temperature.isNotBlank()
            ) {
                Text("Guardar Unidad")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddUnitPreview() {
    RegistroTheme {
        AddUnitScreen(onNavigateBack = {}, onSaveUnit = { _, _ -> })
    }
}
