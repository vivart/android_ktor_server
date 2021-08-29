package com.example.android_ktor_server

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    status: Boolean,
    toggleServer: (Boolean, String) -> Unit,
    launch: (url: String) -> Unit,
) {
    var serverRunning by remember { mutableStateOf(status) }
    var response by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = response,
            onValueChange = { response = it },
            label = { Text(stringResource(R.string.response_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.0f)
                .padding(16.dp),
            enabled = !serverRunning
        )
        ServerScreen(serverRunning, launch) {
            toggleServer(serverRunning, response)
            serverRunning = !serverRunning
        }
    }
}

@Composable
private fun ServerScreen(
    serverRunning: Boolean,
    launch: (url: String) -> Unit,
    onClick: () -> Unit,
) {
    if (serverRunning) StopServer(launch, onClick) else StartServer(onClick)
}

@Composable
private fun StartServer(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp),

        ) {
        Text(text = stringResource(R.string.start_server_button))
    }
}

@Composable
private fun StopServer(launch: (url: String) -> Unit, onClick: () -> Unit) {
    val localhost = stringResource(R.string.localhost)
    Column {
        Button(onClick = { launch(localhost) }) {
            Text(text = stringResource(R.string.open_url, localhost))
        }
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(16.dp),

            ) {
            Text(text = stringResource(R.string.stop_server_button))
        }
    }
}