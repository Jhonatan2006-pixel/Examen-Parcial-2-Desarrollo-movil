package com.example.geotracker.data

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import org.json.JSONObject

// Guarda el token JWT y el nombre del usuario mientras la sesión esté iniciada
class TokenManager(context: Context) {

    private val preferences =
        context.getSharedPreferences("geotracker_session", Context.MODE_PRIVATE)

    fun guardarSesion(token: String, nombre: String) {
        preferences.edit {
            putString("token", token)
            putString("nombre", nombre)
        }
    }

    fun obtenerToken(): String? {
        return preferences.getString("token", null)
    }

    fun obtenerNombre(): String {
        return preferences.getString("nombre", null).orEmpty()
    }

    fun eliminarSesion() {
        preferences.edit {
            remove("token")
            remove("nombre")
        }
    }

    // El token vence a las 2 horas: se revisa el campo "exp" del JWT
    // para no abrir el Dashboard con una sesión que ya expiró
    fun haySesionVigente(): Boolean {

        val token = obtenerToken() ?: return false

        return try {

            val payload = token.split(".")[1]

            val json = JSONObject(
                String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            )

            val expiraEn = json.optLong("exp", 0L) * 1000

            expiraEn > System.currentTimeMillis()

        } catch (e: Exception) {
            false
        }
    }
}
