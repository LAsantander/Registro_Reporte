package com.example.registro.model

data class RefrigeratedUnit(
    val id: String,
    val plateNumber: String,
    val temperature: Double,
    val status: String, // e.g., "Active", "Maintenance", "Off"
    val lastUpdate: String
)
