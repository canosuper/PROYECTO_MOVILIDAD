package com.example.proyectomovilidad

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.ui.DashboardScreen
import com.example.proyectomovilidad.ui.VideoListScreen
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        
        // Estado temporal para la lista de vídeos
        var videos by remember { mutableStateOf(listOf<VideoUpload>()) }

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
                    VideoListScreen(
                        videos = videos,
                        onBack = { currentScreen = "dashboard" },
                        onAddVideo = { uri, durationSeconds ->
                            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            val newVideo = VideoUpload(
                                id = videos.size.toString(),
                                date = now,
                                goalId = "goal_1",
                                videoUrl = uri,
                                durationSeconds = durationSeconds
                            )
                            videos = videos + newVideo
                        }
                    )
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
