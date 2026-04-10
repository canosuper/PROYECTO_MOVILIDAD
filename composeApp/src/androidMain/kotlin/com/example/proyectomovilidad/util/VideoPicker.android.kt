package com.example.proyectomovilidad.util

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si nos dan el permiso, procedemos a crear el archivo y lanzar la cámara
            val file = File(context.cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            tempVideoUri = uri
            cameraLauncher.launch(uri)
        } else {
            onError("Permiso de cámara denegado. No se puede grabar vídeo.")
        }
    }

    return object : VideoPickerLauncher {
        override fun launchGallery() {
            galleryLauncher.launch("video/*")
        }

        override fun launchCamera() {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val file = File(context.cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempVideoUri = uri
                cameraLauncher.launch(uri)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
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

        // Validación de duración: máximo 2 minutos (120.000 ms)
        if (durationMs > 120_000) {
            onError("El vídeo es demasiado largo. El máximo permitido son 2 minutos.")
        } else {
            onVideoSelected(uri.toString(), durationMs)
        }
    } catch (e: Exception) {
        onError("Error al procesar el video: ${e.message}")
    }
}
