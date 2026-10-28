package com.example.geotracker.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.geotracker.R

// Indicador de carga con un texto debajo
@Composable
fun EstadoCargando(
    texto: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        CircularProgressIndicator()

        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Tarjeta informativa (lista vacía) o de error con botón para reintentar
@Composable
fun EstadoMensaje(
    texto: String,
    modifier: Modifier = Modifier,
    esError: Boolean = false,
    onReintentar: (() -> Unit)? = null
) {

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                imageVector = if (esError) Icons.Filled.Warning else Icons.Filled.Info,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (esError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )

            Text(
                text = texto,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            if (onReintentar != null) {
                Button(onClick = onReintentar) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}
