package com.example.geotracker.ui.captura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.LocationRepository
import com.example.geotracker.data.UbicacionRepository
import com.example.geotracker.model.UbicacionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CapturaViewModel(
    private val ubicacionRepository: UbicacionRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CapturaState())

    val state: StateFlow<CapturaState> = _state.asStateFlow()

    fun onNombreChange(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre.take(100), errorNombre = null) }
    }

    fun onDescripcionChange(nuevaDescripcion: String) {
        _state.update {
            it.copy(descripcion = nuevaDescripcion.take(500), errorDescripcion = null)
        }
    }

    // Revisa el formulario antes de pedir el permiso y la ubicación
    fun formularioValido(): Boolean {

        val actual = _state.value

        val errorNombre =
            if (actual.nombre.isBlank()) "Escribe un nombre para la ubicación" else null

        val errorDescripcion =
            if (actual.descripcion.isBlank()) "Escribe una descripción" else null

        _state.update {
            it.copy(errorNombre = errorNombre, errorDescripcion = errorDescripcion)
        }

        return errorNombre == null && errorDescripcion == null
    }

    fun tienePermisoUbicacion(): Boolean = locationRepository.tienePermiso()

    // 1. Obtener la ubicación GPS actual  2. Enviarla al backend
    fun capturarYGuardar() {

        if (_state.value.cargando || !formularioValido()) {
            return
        }

        _state.update {
            it.copy(
                etapa = EtapaCaptura.OBTENIENDO_UBICACION,
                mensajeExito = null,
                mensajeError = null
            )
        }

        viewModelScope.launch {

            val ubicacionGps = locationRepository.obtenerUbicacionActual()

            val (latitud, longitud) = ubicacionGps.getOrElse { error ->
                _state.update {
                    it.copy(
                        etapa = EtapaCaptura.INACTIVA,
                        mensajeError = error.message
                    )
                }
                return@launch
            }

            _state.update { it.copy(etapa = EtapaCaptura.ENVIANDO) }

            val actual = _state.value

            ubicacionRepository.guardarUbicacion(
                UbicacionRequest(
                    nombre = actual.nombre.trim(),
                    descripcion = actual.descripcion.trim(),
                    latitud = latitud,
                    longitud = longitud
                )
            )
                .onSuccess { guardada ->
                    // Formulario limpio para capturar la siguiente ubicación
                    _state.update {
                        CapturaState(
                            mensajeExito = "Ubicación \"${guardada.nombre}\" guardada correctamente",
                            ultimaGuardada = guardada
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            etapa = EtapaCaptura.INACTIVA,
                            mensajeError = error.message
                        )
                    }
                }
        }
    }

    fun onPermisoDenegado() {
        _state.update { it.copy(mostrarDialogoPermiso = true) }
    }

    fun cerrarDialogoPermiso() {
        _state.update { it.copy(mostrarDialogoPermiso = false) }
    }

    // El Snackbar ya mostró el mensaje
    fun mensajeMostrado() {
        _state.update { it.copy(mensajeExito = null, mensajeError = null) }
    }
}
