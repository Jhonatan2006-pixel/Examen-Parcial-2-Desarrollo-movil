package com.example.geotracker.ui.captura

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geotracker.R
import com.example.geotracker.ui.AppViewModelProvider
import com.example.geotracker.ui.componentes.formatearCoordenada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapturaScreen(
    onRegresar: () -> Unit,
    viewModel: CapturaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    // Permiso de ubicación en tiempo de ejecución
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->

        val concedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (concedido) {
            viewModel.capturarYGuardar()
        } else {
            viewModel.onPermisoDenegado()
        }
    }

    fun onCapturarClic() {

        if (!viewModel.formularioValido()) {
            return
        }

        if (viewModel.tienePermisoUbicacion()) {
            viewModel.capturarYGuardar()
        } else {
            permisoLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Mensaje de éxito o error según la respuesta del backend
    LaunchedEffect(state.mensajeExito, state.mensajeError) {
        val mensaje = state.mensajeExito ?: state.mensajeError
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            viewModel.mensajeMostrado()
        }
    }

    if (state.mostrarDialogoPermiso) {

        AlertDialog(
            onDismissRequest = viewModel::cerrarDialogoPermiso,
            icon = { Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null) },
            title = { Text(stringResource(R.string.permission_title)) },
            text = { Text(stringResource(R.string.permission_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cerrarDialogoPermiso()
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null)
                            )
                        )
                    }
                ) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cerrarDialogoPermiso) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.capture_title)) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = stringResource(R.string.capture_help),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text(stringResource(R.string.location_name)) },
                isError = state.errorNombre != null,
                supportingText = state.errorNombre?.let { error -> { Text(error) } },
                singleLine = true,
                enabled = !state.cargando,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text(stringResource(R.string.location_description)) },
                isError = state.errorDescripcion != null,
                supportingText = state.errorDescripcion?.let { error -> { Text(error) } },
                minLines = 3,
                maxLines = 5,
                enabled = !state.cargando,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = ::onCapturarClic,
                enabled = !state.cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {

                if (state.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.capture_button))
                }
            }

            // Indicador de carga durante la captura GPS y el envío
            if (state.cargando) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                    Text(
                        text = stringResource(
                            if (state.etapa == EtapaCaptura.OBTENIENDO_UBICACION) {
                                R.string.capture_getting_location
                            } else {
                                R.string.capture_sending
                            }
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            state.ultimaGuardada?.let { ubicacion ->

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.capture_last_saved),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Text(text = ubicacion.nombre, style = MaterialTheme.typography.bodyLarge)

                        Text(
                            text = stringResource(
                                R.string.latitude_value,
                                formatearCoordenada(ubicacion.latitud)
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = stringResource(
                                R.string.longitude_value,
                                formatearCoordenada(ubicacion.longitud)
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
