package com.example.geotracker.ui.mapa

import com.example.geotracker.model.Ubicacion

data class MapaState(
    val cargando: Boolean = false,
    val ubicaciones: List<Ubicacion> = emptyList(),
    val error: String? = null,

    // Marcador presionado: si no es null se muestra el AlertDialog con sus datos
    val ubicacionSeleccionada: Ubicacion? = null
) {
    val mostrarDialogo: Boolean
        get() = ubicacionSeleccionada != null
}
