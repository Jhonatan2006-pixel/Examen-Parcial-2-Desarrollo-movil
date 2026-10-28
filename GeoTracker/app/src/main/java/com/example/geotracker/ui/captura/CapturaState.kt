package com.example.geotracker.ui.captura

import com.example.geotracker.model.Ubicacion

// Paso en el que va la captura, para mostrar el indicador de carga adecuado
enum class EtapaCaptura {
    INACTIVA,
    OBTENIENDO_UBICACION,
    ENVIANDO
}

data class CapturaState(
    val nombre: String = "",
    val descripcion: String = "",
    val errorNombre: String? = null,
    val errorDescripcion: String? = null,
    val etapa: EtapaCaptura = EtapaCaptura.INACTIVA,
    val mensajeExito: String? = null,
    val mensajeError: String? = null,
    val mostrarDialogoPermiso: Boolean = false,
    val ultimaGuardada: Ubicacion? = null
) {
    val cargando: Boolean
        get() = etapa != EtapaCaptura.INACTIVA
}
