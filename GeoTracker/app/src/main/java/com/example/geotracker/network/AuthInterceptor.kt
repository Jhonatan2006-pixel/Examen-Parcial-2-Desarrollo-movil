package com.example.geotracker.network

import com.example.geotracker.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

// Agrega "Authorization: Bearer <token>" a todas las peticiones
// cuando hay una sesión iniciada
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = tokenManager.obtenerToken()

        val peticion = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(peticion)
    }
}
