package com.example.geotracker.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.geotracker.GeoTrackerApp
import com.example.geotracker.ui.captura.CapturaViewModel
import com.example.geotracker.ui.dashboard.DashboardViewModel
import com.example.geotracker.ui.listado.ListadoViewModel
import com.example.geotracker.ui.login.LoginViewModel
import com.example.geotracker.ui.mapa.MapaViewModel

// Fábrica única de ViewModels: les entrega los repositorios creados en GeoTrackerApp
object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            LoginViewModel(geoTrackerApp().authRepository)
        }

        initializer {
            DashboardViewModel(geoTrackerApp().authRepository)
        }

        initializer {
            CapturaViewModel(
                geoTrackerApp().ubicacionRepository,
                geoTrackerApp().locationRepository
            )
        }

        initializer {
            ListadoViewModel(geoTrackerApp().ubicacionRepository)
        }

        initializer {
            MapaViewModel(geoTrackerApp().ubicacionRepository)
        }
    }
}

private fun CreationExtras.geoTrackerApp(): GeoTrackerApp =
    this[APPLICATION_KEY] as GeoTrackerApp
