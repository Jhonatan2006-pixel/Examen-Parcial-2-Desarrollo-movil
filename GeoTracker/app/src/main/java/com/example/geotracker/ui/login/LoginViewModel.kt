package com.example.geotracker.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(nuevoEmail: String) {
        _state.update { it.copy(email = nuevoEmail, error = null) }
    }

    fun onPasswordChange(nuevoPassword: String) {
        _state.update { it.copy(password = nuevoPassword, error = null) }
    }

    fun onLoginClic() {

        val actual = _state.value

        if (actual.email.isBlank() || actual.password.isBlank()) {
            _state.update { it.copy(error = "Ingresa tu correo y tu contraseña") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(actual.email.trim()).matches()) {
            _state.update { it.copy(error = "El correo no tiene un formato válido") }
            return
        }

        _state.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {

            authRepository.login(actual.email, actual.password)
                .onSuccess {
                    _state.update { it.copy(cargando = false, loginExitoso = true) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            cargando = false,
                            error = error.message ?: "No se pudo iniciar sesión"
                        )
                    }
                }
        }
    }

    // Se llama después de navegar al Dashboard
    fun onNavegacionRealizada() {
        _state.value = LoginState()
    }
}
