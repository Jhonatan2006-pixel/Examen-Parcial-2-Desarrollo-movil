package com.example.geotracker.ui.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.UbicacionRepository
import com.example.geotracker.model.Ubicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapaState())

    val state: StateFlow<MapaState> = _state.asStateFlow()

    // Al entrar a la pantalla se consultan las ubicaciones del backend
    init {
        cargarUbicaciones()
    }

    fun cargarUbicaciones() {

        _state.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {

            ubicacionRepository.obtenerUbicaciones()
                .onSuccess { ubicaciones ->
                    _state.update { it.copy(cargando = false, ubicaciones = ubicaciones) }
                }
                .onFailure { error ->
                    _state.update { it.copy(cargando = false, error = error.message) }
                }
        }
    }

    // Se presionó un marcador: abre el AlertDialog con esa ubicación
    fun seleccionarUbicacion(ubicacion: Ubicacion) {
        _state.update { it.copy(ubicacionSeleccionada = ubicacion) }
    }

    fun cerrarDialogo() {
        _state.update { it.copy(ubicacionSeleccionada = null) }
    }
}
