package com.example.geotracker.data

import com.example.geotracker.model.LoginRequest
import com.example.geotracker.model.UsuarioResponse
import com.example.geotracker.network.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    // Autentica contra el backend y guarda el token para las siguientes peticiones
    suspend fun login(
        email: String,
        password: String
    ): Result<UsuarioResponse> {

        return try {

            val response = apiService.login(
                LoginRequest(
                    email = email.trim(),
                    password = password
                )
            )

            if (response.success && response.data != null && response.token != null) {

                tokenManager.guardarSesion(
                    token = response.token,
                    nombre = response.data.nombre
                )

                Result.success(response.data)

            } else {

                Result.failure(ErrorApp(response.message))
            }

        } catch (e: Exception) {

            Result.failure(
                ErrorApp(mensajeDeError(e, "No se pudo iniciar sesión"))
            )
        }
    }

    fun haySesionVigente(): Boolean = tokenManager.haySesionVigente()

    fun nombreUsuario(): String = tokenManager.obtenerNombre()

    fun cerrarSesion() {
        tokenManager.eliminarSesion()
    }
}
