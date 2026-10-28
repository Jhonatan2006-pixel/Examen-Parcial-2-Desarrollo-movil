package com.example.geotracker.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val telefono: String?
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UsuarioResponse?,
    val token: String?
)
