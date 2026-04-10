package com.example.proyectomovilidad.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.util.rememberVideoPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    videos: List<VideoUpload>,
    onBack: () -> Unit,
    onAddVideo: (uri: String, durationSeconds: Int) -> Unit
) {
    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    val videoPicker = rememberVideoPicker(
        onVideoSelected = { uri, durationMs ->
            val seconds = (durationMs / 1000).toInt()
            if (seconds > 120) {
                showErrorDialog = "El vídeo es demasiado largo (${seconds} seg). El máximo permitido son 2 minutos (120 seg)."
            } else {
                onAddVideo(uri, seconds)
            }
            showSheet = false
        },
        onError = { error ->
            showErrorDialog = error
            showSheet = false
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Vídeos (Quincenal)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Vídeo")
            }
        }
    ) { padding ->
        if (showErrorDialog != null) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = null },
                title = { Text("Atención") },
                text = { Text(showErrorDialog!!) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = null }) {
                        Text("Entendido")
                    }
                }
            )
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    ListItem(
                        headlineContent = { Text("Grabar nuevo vídeo") },
                        leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                        modifier = Modifier.clickable { videoPicker.launchCamera() }
                    )
                    ListItem(
                        headlineContent = { Text("Elegir de la galería") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                        modifier = Modifier.clickable { videoPicker.launchGallery() }
                    )
                }
            }
        }

        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no has subido ningún vídeo.\nRecuerda subir uno cada 15 días.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Historial de grabaciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(videos) { video ->
                    VideoItem(video)
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: VideoUpload) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Vídeo del ${video.date}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Duración: ${video.durationSeconds} segundos",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
