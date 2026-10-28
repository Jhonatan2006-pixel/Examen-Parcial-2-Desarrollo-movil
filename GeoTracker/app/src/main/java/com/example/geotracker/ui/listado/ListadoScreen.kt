package com.example.geotracker.ui.listado

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    onRegresar: () -> Unit,
    onVerMapa: () -> Unit,
    viewModel: ListadoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.list_title)) },
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
        },
        floatingActionButton = {
            if (state.ubicaciones.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onVerMapa,
                    icon = { Icon(imageVector = Icons.Filled.Place, contentDescription = null) },
                    text = { Text(stringResource(R.string.list_view_map)) }
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            when {

                state.cargando -> EstadoCargando(texto = stringResource(R.string.list_loading))

                state.error != null -> EstadoMensaje(
                    texto = state.error.orEmpty(),
                    esError = true,
                    onReintentar = viewModel::cargarUbicaciones
                )

                state.ubicaciones.isEmpty() -> EstadoMensaje(
                    texto = stringResource(R.string.list_empty)
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.ubicaciones, key = { it.id }) { ubicacion ->
                        UbicacionCard(ubicacion = ubicacion)
                    }
                }
            }
        }
    }
}

// Card de Material 3 con el nombre, la descripción y las coordenadas
@Composable
private fun UbicacionCard(ubicacion: Ubicacion) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = ubicacion.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = ubicacion.descripcion,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            Text(
                text = stringResource(
                    R.string.latitude_value,
                    formatearCoordenada(ubicacion.latitud)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(
                    R.string.longitude_value,
                    formatearCoordenada(ubicacion.longitud)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val fecha = formatearFecha(ubicacion.fechaCreacion)

            if (fecha.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.created_at_value, fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
