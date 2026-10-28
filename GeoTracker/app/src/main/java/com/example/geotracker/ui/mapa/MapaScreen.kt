package com.example.geotracker.ui.mapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.R
import com.example.geotracker.model.Ubicacion
import com.example.geotracker.ui.AppViewModelProvider
import com.example.geotracker.ui.componentes.EstadoCargando
import com.example.geotracker.ui.componentes.EstadoMensaje
import com.example.geotracker.ui.componentes.formatearCoordenada
import com.example.geotracker.ui.componentes.formatearFecha
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// Vista inicial mientras cargan las ubicaciones: Guatemala
private val CENTRO_INICIAL = LatLng(14.6349, -90.5069)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    onRegresar: () -> Unit,
    viewModel: MapaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CENTRO_INICIAL, 7f)
    }

    // La cámara solo se puede mover con límites cuando el mapa ya cargó
    var mapaCargado by remember { mutableStateOf(false) }

    // Centrar la cámara para que todos los marcadores queden visibles
    LaunchedEffect(mapaCargado, state.ubicaciones) {

        if (!mapaCargado || state.ubicaciones.isEmpty()) {
            return@LaunchedEffect
        }

        val actualizacion = if (state.ubicaciones.size == 1) {

            val unica = state.ubicaciones.first()
            CameraUpdateFactory.newLatLngZoom(LatLng(unica.latitud, unica.longitud), 15f)

        } else {

            val limites = LatLngBounds.builder().apply {
                state.ubicaciones.forEach { include(LatLng(it.latitud, it.longitud)) }
            }.build()

            CameraUpdateFactory.newLatLngBounds(limites, 150)
        }

        cameraPositionState.animate(actualizacion, 1000)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title)) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::cargarUbicaciones,
                        enabled = !state.cargando
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    mapToolbarEnabled = false
                ),
                onMapLoaded = { mapaCargado = true }
            ) {

                state.ubicaciones.forEach { ubicacion ->

                    key(ubicacion.id) {

                        // Sin title ni snippet y con onClick que devuelve true:
                        // Google Maps no abre su InfoWindow predeterminado y
                        // en su lugar se muestra el AlertDialog de Material 3
                        Marker(
                            state = remember {
                                MarkerState(position = LatLng(ubicacion.latitud, ubicacion.longitud))
                            },
                            onClick = {
                                viewModel.seleccionarUbicacion(ubicacion)
                                true
                            }
                        )
                    }
                }
            }

            when {

                state.cargando -> EstadoCargando(
                    texto = stringResource(R.string.list_loading),
                    modifier = Modifier.align(Alignment.Center)
                )

                state.error != null -> EstadoMensaje(
                    texto = state.error.orEmpty(),
                    esError = true,
                    onReintentar = viewModel::cargarUbicaciones,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Mensaje informativo cuando no hay ubicaciones
                state.ubicaciones.isEmpty() -> EstadoMensaje(
                    texto = stringResource(R.string.map_empty),
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.map_count, state.ubicaciones.size)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Place, contentDescription = null)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }
        }
    }

    // El estado del diálogo (visible y ubicación seleccionada) vive en el ViewModel
    state.ubicacionSeleccionada?.let { ubicacion ->
        DialogoUbicacion(
            ubicacion = ubicacion,
            onCerrar = viewModel::cerrarDialogo
        )
    }
}

@Composable
private fun DialogoUbicacion(
    ubicacion: Ubicacion,
    onCerrar: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(imageVector = Icons.Filled.Place, contentDescription = null) },
        title = { Text(ubicacion.nombre) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                DatoDialogo(
                    titulo = stringResource(R.string.dialog_description),
                    valor = ubicacion.descripcion
                )

                DatoDialogo(
                    titulo = stringResource(R.string.dialog_coordinates),
                    valor = stringResource(
                        R.string.latitude_value,
                        formatearCoordenada(ubicacion.latitud)
                    ) + "\n" + stringResource(
                        R.string.longitude_value,
                        formatearCoordenada(ubicacion.longitud)
                    )
                )

                val fecha = formatearFecha(ubicacion.fechaCreacion)

                if (fecha.isNotEmpty()) {
                    DatoDialogo(
                        titulo = stringResource(R.string.dialog_date),
                        valor = fecha
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun DatoDialogo(
    titulo: String,
    valor: String
) {

    Column {

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
