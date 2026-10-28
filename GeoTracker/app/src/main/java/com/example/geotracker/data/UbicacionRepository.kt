package com.example.geotracker.data

import com.example.geotracker.model.Ubicacion
import com.example.geotracker.model.UbicacionRequest
import com.example.geotracker.network.ApiService

class UbicacionRepository(
    private val apiService: ApiService
) {

    suspend fun guardarUbicacion(
        request: UbicacionRequest
    ): Result<Ubicacion> {

        return try {

            val response = apiService.guardarUbicacion(request)

            if (response.success && response.data != null) {

                Result.success(response.data)

            } else {

                Result.failure(ErrorApp(response.message))
            }

        } catch (e: Exception) {

            Result.failure(
                ErrorApp(mensajeDeError(e, "No se pudo guardar la ubicación"))
            )
        }
    }

    // Ubicaciones del usuario autenticado (el backend las filtra por el token)
    suspend fun obtenerUbicaciones(): Result<List<Ubicacion>> {

        return try {

            val response = apiService.obtenerUbicaciones()

            if (response.success) {

                Result.success(response.data.orEmpty())

            } else {

                Result.failure(ErrorApp(response.message))
            }

        } catch (e: Exception) {

            Result.failure(
                ErrorApp(mensajeDeError(e, "No se pudieron cargar las ubicaciones"))
            )
        }
    }
}
