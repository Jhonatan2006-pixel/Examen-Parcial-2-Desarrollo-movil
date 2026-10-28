package com.example.geotracker.ui.listado

import com.example.geotracker.model.Ubicacion

data class ListadoState(
    val cargando: Boolean = false,
    val ubicaciones: List<Ubicacion> = emptyList(),
    val error: String? = null
)
