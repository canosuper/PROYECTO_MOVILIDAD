package com.example.proyectomovilidad

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.ui.AlpScreen
import com.example.proyectomovilidad.ui.DashboardScreen
import com.example.proyectomovilidad.ui.GasScreen
import com.example.proyectomovilidad.ui.VideoListScreen
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val LightColors = lightColorScheme(
    primary = Color(0xFF1976D2), // Azul Profesional
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF388E3C), // Verde
    onSecondary = Color.White,
    tertiary = Color(0xFFFBC02D), // Ámbar
    background = Color(0xFFF8F9FA), // Gris neutro muy claro
    surface = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val videoViewModel = remember { com.example.proyectomovilidad.viewmodel.VideoViewModel() }
    val userName by videoViewModel.userName.collectAsState()

    MaterialTheme(
        colorScheme = LightColors,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp)
        )
    ) {
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
                        userName = userName,
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
                    AlpScreen(onBack = { currentScreen = "dashboard" })
                }
                "gas" -> {
                    GasScreen(onBack = { currentScreen = "dashboard" })
                }
                "video" -> {
                    VideoListScreen(
                        viewModel = videoViewModel,
                        onBack = { currentScreen = "dashboard" }
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
