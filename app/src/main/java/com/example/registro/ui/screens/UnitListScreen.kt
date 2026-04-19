package com.example.registro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.registro.model.RefrigeratedUnit
import com.example.registro.ui.theme.RegistroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(
    units: List<RefrigeratedUnit>,
    onAddUnitClick: () -> Unit,
    onUnitClick: (RefrigeratedUnit) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unidades Refrigeradas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUnitClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Unidad")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(units) { unit ->
                UnitItem(unit = unit, onClick = { onUnitClick(unit) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitListPreview() {
    RegistroTheme {
        UnitListScreen(
            units = listOf(
                RefrigeratedUnit("1", "ABC-123", 4.5, "Activo", "Hoy"),
                RefrigeratedUnit("2", "XYZ-789", -18.0, "Mantenimiento", "Ayer")
            ),
            onAddUnitClick = {},
            onUnitClick = {}
        )
    }
}

@Composable
fun UnitItem(unit: RefrigeratedUnit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Placa: ${unit.plateNumber}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Temperatura: ${unit.temperature}°C", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${unit.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
