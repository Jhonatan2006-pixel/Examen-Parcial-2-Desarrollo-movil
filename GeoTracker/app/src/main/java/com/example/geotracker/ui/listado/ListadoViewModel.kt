package com.example.geotracker.ui.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.UbicacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListadoViewModel(
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ListadoState())

    val state: StateFlow<ListadoState> = _state.asStateFlow()

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
}
