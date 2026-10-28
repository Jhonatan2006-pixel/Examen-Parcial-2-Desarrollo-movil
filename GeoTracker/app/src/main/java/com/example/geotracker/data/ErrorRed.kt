package com.example.geotracker.data

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Error de la app que ya trae el texto que se le muestra al usuario
class ErrorApp(mensaje: String) : Exception(mensaje)

// Convierte un error de red en el texto que se le muestra al usuario
fun mensajeDeError(
    error: Throwable,
    porDefecto: String
): String {

    return when (error) {

        is ErrorApp -> error.message ?: porDefecto

        // El backend respondió con un error (400, 401, 500...):
        // se usa el mensaje que viene en la respuesta
        is HttpException -> {

            val mensaje = try {

                val cuerpo = error.response()?.errorBody()?.string().orEmpty()

                JSONObject(cuerpo).optString("message")

            } catch (e: Exception) {
                ""
            }

            when {
                error.code() == 401 && mensaje.contains("Token") ->
                    "Tu sesión expiró. Inicia sesión de nuevo"

                mensaje.isNotBlank() -> mensaje

                else -> porDefecto
            }
        }

        is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión a internet"

        else -> porDefecto
    }
}
