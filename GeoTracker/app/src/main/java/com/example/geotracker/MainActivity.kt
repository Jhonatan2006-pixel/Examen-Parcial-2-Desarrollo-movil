package com.example.geotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.geotracker.ui.navigation.AppNavigation
import com.example.geotracker.ui.theme.GeoTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // La app dibuja detrás de las barras del sistema y los iconos
        // de la barra de estado se adaptan al tema claro u oscuro
        enableEdgeToEdge()

        // Si hay un token vigente se entra directo al Dashboard
        val haySesion = (application as GeoTrackerApp).authRepository.haySesionVigente()

        setContent {
            GeoTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(haySesion = haySesion)
                }
            }
        }
    }
}
