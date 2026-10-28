package com.example.geotracker.ui.componentes

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// Coordenada con 6 decimales (precisión de unos 10 cm)
fun formatearCoordenada(valor: Double): String =
    String.format(Locale.US, "%.6f", valor)

private val formatoFecha =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())

// El backend envía la fecha en formato ISO 8601 (UTC);
// se muestra en la hora local del teléfono
fun formatearFecha(fechaIso: String?): String {

    if (fechaIso.isNullOrBlank()) {
        return ""
    }

    return try {
        OffsetDateTime.parse(fechaIso)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(formatoFecha)
    } catch (e: Exception) {
        fechaIso
    }
}
