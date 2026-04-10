package com.example.proyectomovilidad.util

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberVideoPicker(
    onVideoSelected: (uri: String, durationMs: Long) -> Unit,
    onError: (String) -> Unit
): VideoPickerLauncher {
    val context = LocalContext.current
    var tempVideoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            processVideo(context, uri, onVideoSelected, onError)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success: Boolean ->
        if (success && tempVideoUri != null) {
            processVideo(context, tempVideoUri!!, onVideoSelected, onError)
        }
    }

    return object : VideoPickerLauncher {
        override fun launchGallery() {
            galleryLauncher.launch("video/*")
        }

        override fun launchCamera() {
            val file = File(context.cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            tempVideoUri = uri
            cameraLauncher.launch(uri)
        }
    }
}

private fun processVideo(
    context: android.content.Context,
    uri: Uri,
    onVideoSelected: (String, Long) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = time?.toLong() ?: 0L
        retriever.release()
        onVideoSelected(uri.toString(), durationMs)
    } catch (e: Exception) {
        onError("Error al procesar el video: ${e.message}")
    }
}
