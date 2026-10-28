package com.example.geotracker

import android.app.Application
import com.example.geotracker.data.AuthRepository
import com.example.geotracker.data.LocationRepository
import com.example.geotracker.data.TokenManager
import com.example.geotracker.data.UbicacionRepository
import com.example.geotracker.network.RetrofitClient

// Crea una sola vez las dependencias que comparten los ViewModels
class GeoTrackerApp : Application() {

    lateinit var authRepository: AuthRepository
        private set

    lateinit var ubicacionRepository: UbicacionRepository
        private set

    lateinit var locationRepository: LocationRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.crearApiService(tokenManager)

        authRepository = AuthRepository(apiService, tokenManager)
        ubicacionRepository = UbicacionRepository(apiService)
        locationRepository = LocationRepository(this)
    }
}
