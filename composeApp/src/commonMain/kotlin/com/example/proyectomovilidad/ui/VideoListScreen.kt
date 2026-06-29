package com.example.proyectomovilidad.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.util.rememberVideoPicker
import com.example.proyectomovilidad.viewmodel.VideoViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    viewModel: VideoViewModel,
    userId: String,
    onBack: () -> Unit,
) {
    val videos by viewModel.videos.collectAsState()
    val isUploadingGlobal by viewModel.isUploadingGlobal.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val listState = rememberLazyListState()
    val canScrollDown by remember { derivedStateOf { listState.canScrollForward } }
    
    var selectedVideoUrl by mutableStateOf<String?>(null)
    var videoToDelete by mutableStateOf<VideoUpload?>(null)
    var showErrorDialog by mutableStateOf<String?>(null)
    var showSheet by mutableStateOf(false)
    val sheetState = rememberModalBottomSheetState()
    
    val videoPicker = rememberVideoPicker(
        onVideoSelected = { uri, durationMs ->
            val seconds = (durationMs / 1000).toInt()
            viewModel.uploadVideo(uri, seconds, userId)
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
            if (!isUploadingGlobal) {
                FloatingActionButton(onClick = { showSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Vídeo")
                }
            }
        }
    ) { padding ->
        // Diálogo para errores (incluyendo timeout)
        errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissError() },
                title = { Text("Aviso de Subida") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissError() }) {
                        Text("Entendido")
                    }
                }
            )
        }

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

        if (selectedVideoUrl != null) {
            AlertDialog(
                onDismissRequest = { selectedVideoUrl = null },
                title = { Text("Reproductor") },
                text = {
                    VideoPlayer(
                        url = selectedVideoUrl!!,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                },
                confirmButton = {
                    TextButton(onClick = { selectedVideoUrl = null }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        if (videoToDelete != null) {
            AlertDialog(
                onDismissRequest = { videoToDelete = null },
                title = { Text("¿Eliminar vídeo?") },
                text = { Text("Esta acción borrará el vídeo del historial y del servidor. No se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteVideo(videoToDelete!!, userId)
                            videoToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { videoToDelete = null }) {
                        Text("Cancelar")
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

        if (videos.isEmpty() && !isUploadingGlobal) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Black, Color.Transparent),
                                    startY = size.height * 0.90f,
                                    endY = size.height
                                ),
                                blendMode = BlendMode.DstIn
                            )
                        },
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
                ) {
                    if (isUploadingGlobal) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "Subiendo vídeo...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    LinearProgressIndicator(
                                        progress = uploadProgress,
                                        modifier = Modifier.fillMaxWidth(),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "No cierres la aplicación",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = "${(uploadProgress * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Historial (${videos.size} vídeos)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(videos, key = { it.id }) { video ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    videoToDelete = video
                                    false
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                    else -> Color.Transparent
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 4.dp)
                                        .background(color, MaterialTheme.shapes.medium),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Borrar",
                                        modifier = Modifier.padding(end = 16.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        ) {
                            VideoItem(video, onClick = {
                                if (!video.isUploading && video.videoUrl.isNotEmpty()) {
                                    selectedVideoUrl = video.videoUrl
                                }
                            })
                        }
                    }
                }

                if (canScrollDown && videos.size > 2) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraLarge,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ver más vídeos",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: VideoUpload, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !video.isUploading) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (video.isUploading) Icons.Default.CloudUpload else Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (video.isUploading) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                val formattedDate = remember(video.timestamp) {
                    if (video.timestamp == 0L) "Vídeo del ${video.date}"
                    else {
                        val instant = Instant.fromEpochMilliseconds(video.timestamp)
                        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        val mes = when (dt.month.ordinal + 1) {
                            1 -> "enero"; 2 -> "febrero"; 3 -> "marzo"; 4 -> "abril"
                            5 -> "mayo"; 6 -> "junio"; 7 -> "julio"; 8 -> "agosto"
                            9 -> "septiembre"; 10 -> "octubre"; 11 -> "noviembre"; 12 -> "diciembre"
                            else -> ""
                        }
                        "${dt.dayOfMonth} de $mes de ${dt.year}, ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
                    }
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (video.isUploading) "Subiendo..." else "Duración: ${video.durationSeconds} segundos",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
