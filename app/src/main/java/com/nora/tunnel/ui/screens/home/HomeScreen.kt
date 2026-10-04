package com.nora.tunnel.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nora.tunnel.tunnel.ConnectionState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "NORA TUNNEL",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Secure. Private. Connected.",
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = when (state) {
                ConnectionState.CONNECTED ->
                    "● CONNECTED"

                ConnectionState.ERROR ->
                    "● ERROR"

                else ->
                    "● DISCONNECTED"
            }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        when (state) {

            ConnectionState.IDLE,
            ConnectionState.DISCONNECTED -> {

                Button(
                    onClick = {
                        viewModel.connect()
                    },
                    enabled = viewModel.selectedProfile != null
                ) {
                    Text("CONNECT")
                }

                if (viewModel.selectedProfile == null) {
                    Text(
                        text = "No configured server"
                    )
                }
            }

            ConnectionState.CONNECTED -> {

                Text(
                    text = "↓ ${stats.rxBytes.formatBytes()}  ↑ ${stats.txBytes.formatBytes()}"
                )

                Text(
                    text = "Uptime: ${stats.uptimeSec.formatDuration()}"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedButton(
                    onClick = {
                        viewModel.disconnect()
                    }
                ) {
                    Text("DISCONNECT")
                }
            }

            else -> {

                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(state.name)

                OutlinedButton(
                    onClick = {
                        viewModel.disconnect()
                    }
                ) {
                    Text("DISCONNECT")
                }
            }
        }
    }
}

private fun Long.formatBytes(): String {

    if (this < 1024) {
        return "$this B"
    }

    if (this < 1024 * 1024) {
        return "%.1f KB".format(
            this / 1024.0
        )
    }

    if (this < 1024 * 1024 * 1024) {
        return "%.1f MB".format(
            this / (1024.0 * 1024.0)
        )
    }

    return "%.2f GB".format(
        this / (1024.0 * 1024.0 * 1024.0)
    )
}

private fun Long.formatDuration(): String {

    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return "%02d:%02d:%02d".format(
        hours,
        minutes,
        seconds
    )
}
