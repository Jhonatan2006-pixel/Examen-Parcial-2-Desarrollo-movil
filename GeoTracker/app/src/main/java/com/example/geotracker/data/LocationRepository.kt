package com.example.geotracker.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

// Obtiene la ubicación GPS actual con FusedLocationProviderClient
class LocationRepository(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun tienePermiso(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun obtenerUbicacionActual(): Result<Pair<Double, Double>> {

        if (!tienePermiso()) {
            return Result.failure(ErrorApp("Se necesita el permiso de ubicación"))
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            return Result.failure(ErrorApp("Activa la ubicación del dispositivo e intenta de nuevo"))
        }

        return try {

            val cancelacion = CancellationTokenSource()

            // Ubicación nueva de alta precisión; si el GPS no responde
            // se usa la última ubicación conocida del dispositivo
            val ubicacion = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelacion.token)
                .await()
                ?: fusedLocationClient.lastLocation.await()

            if (ubicacion != null) {
                Result.success(Pair(ubicacion.latitude, ubicacion.longitude))
            } else {
                Result.failure(ErrorApp("No se pudo obtener la ubicación. Intenta de nuevo"))
            }

        } catch (e: SecurityException) {

            Result.failure(ErrorApp("Se necesita el permiso de ubicación"))

        } catch (e: Exception) {

            Result.failure(ErrorApp("No se pudo obtener la ubicación. Intenta de nuevo"))
        }
    }
}
