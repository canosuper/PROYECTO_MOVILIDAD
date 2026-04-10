package com.example.proyectomovilidad

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectomovilidad.ui.DashboardScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "dashboard" -> {
                    DashboardScreen(
                        onNavigateToLoan = { currentScreen = "loan" },
                        onNavigateToAlp = { currentScreen = "alp" },
                        onNavigateToGas = { currentScreen = "gas" },
                        onNavigateToVideo = { currentScreen = "video" }
                    )
                }
                "loan" -> {
                    PlaceholderScreen("Documento de Compromiso") { currentScreen = "dashboard" }
                }
                "alp" -> {
                    PlaceholderScreen("Escala ALP") { currentScreen = "dashboard" }
                }
                "gas" -> {
                    PlaceholderScreen("Escala GAS") { currentScreen = "dashboard" }
                }
                "video" -> {
                    PlaceholderScreen("Subir Video") { currentScreen = "dashboard" }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de $title en construcción")
        }
    }
}
