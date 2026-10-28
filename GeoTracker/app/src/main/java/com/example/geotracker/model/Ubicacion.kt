package com.example.geotracker.model

import com.google.gson.annotations.SerializedName

// Ubicación de interés tal como la devuelve el backend
data class Ubicacion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,

    @SerializedName("created_at")
    val fechaCreacion: String?
)

// Datos que la app envía al backend para guardar una ubicación
data class UbicacionRequest(
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double
)

// Formato común de las respuestas del backend: { success, message, data }
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
