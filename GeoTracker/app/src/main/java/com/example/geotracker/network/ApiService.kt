package com.example.geotracker.network

import com.example.geotracker.model.ApiResponse
import com.example.geotracker.model.LoginRequest
import com.example.geotracker.model.LoginResponse
import com.example.geotracker.model.Ubicacion
import com.example.geotracker.model.UbicacionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints del backend. El token no se pasa aquí:
// lo agrega AuthInterceptor a cada petición
interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/ubicaciones")
    suspend fun guardarUbicacion(
        @Body request: UbicacionRequest
    ): ApiResponse<Ubicacion>

    @GET("api/ubicaciones")
    suspend fun obtenerUbicaciones(): ApiResponse<List<Ubicacion>>
}
