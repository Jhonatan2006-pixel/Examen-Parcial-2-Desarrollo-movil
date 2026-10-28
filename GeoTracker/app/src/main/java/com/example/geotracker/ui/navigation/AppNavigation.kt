package com.example.geotracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geotracker.ui.captura.CapturaScreen
import com.example.geotracker.ui.dashboard.DashboardScreen
import com.example.geotracker.ui.listado.ListadoScreen
import com.example.geotracker.ui.login.LoginScreen
import com.example.geotracker.ui.mapa.MapaScreen

object Rutas {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val CAPTURA = "captura"
    const val LISTADO = "listado"
    const val MAPA = "mapa"
}

@Composable
fun AppNavigation(
    haySesion: Boolean
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (haySesion) Rutas.DASHBOARD else Rutas.LOGIN
    ) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    // Al regresar desde el Dashboard no se vuelve al login
                    navController.navigate(Rutas.DASHBOARD) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                onCapturar = { navController.navigate(Rutas.CAPTURA) },
                onListado = { navController.navigate(Rutas.LISTADO) },
                onMapa = { navController.navigate(Rutas.MAPA) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.CAPTURA) {
            CapturaScreen(
                onRegresar = { navController.popBackStack() }
            )
        }

        composable(Rutas.LISTADO) {
            ListadoScreen(
                onRegresar = { navController.popBackStack() },
                onVerMapa = { navController.navigate(Rutas.MAPA) }
            )
        }

        composable(Rutas.MAPA) {
            MapaScreen(
                onRegresar = { navController.popBackStack() }
            )
        }
    }
}
