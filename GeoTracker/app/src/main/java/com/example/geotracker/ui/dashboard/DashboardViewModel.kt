package com.example.geotracker.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.geotracker.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardState(
    val nombreUsuario: String = ""
)

class DashboardViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        DashboardState(nombreUsuario = authRepository.nombreUsuario())
    )

    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
