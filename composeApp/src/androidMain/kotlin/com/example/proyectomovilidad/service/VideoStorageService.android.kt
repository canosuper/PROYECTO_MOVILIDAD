package com.example.proyectomovilidad.service

import android.net.Uri
import com.example.proyectomovilidad.MainActivity
import dev.gitlive.firebase.storage.File
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.effect.Presentation
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import kotlinx.coroutines.CompletableDeferred
import java.io.File as JavaFile

actual suspend fun createFirebaseFile(uri: String): File {
    val context = MainActivity.instance
    val inputUri = Uri.parse(uri)
    
    val outputDir = context.cacheDir
    val outputFile = JavaFile(outputDir, "compressed_${System.currentTimeMillis()}.mp4")
    
    // Forzamos el uso de H.264 que es el estándar más eficiente para compatibilidad y peso
    val transformer = Transformer.Builder(context)
        .setVideoMimeType(MimeTypes.VIDEO_H264)
        .build()

    // Aplicamos un efecto de "Presentación" para forzar la altura a 720p (HD estándar)
    // Esto es lo que realmente reduce el peso de forma masiva
    val effects = Effects(
        emptyList(),
        listOf(Presentation.createForHeight(720))
    )

    val mediaItem = MediaItem.fromUri(inputUri)
    val editedMediaItem = EditedMediaItem.Builder(mediaItem)
        .setEffects(effects)
        .build()

    val deferred = CompletableDeferred<Unit>()

    val listener = object : Transformer.Listener {
        override fun onCompleted(composition: Composition, exportResult: ExportResult) {
            deferred.complete(Unit)
        }

        override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
            println("ERROR EN COMPRESIÓN: ${exportException.message}")
            deferred.completeExceptionally(exportException)
        }
    }

    transformer.addListener(listener)
    
    return try {
        println("Iniciando procesamiento de vídeo (720p H.264)...")
        transformer.start(editedMediaItem, outputFile.absolutePath)
        
        deferred.await()
        
        val originalSize = getUriSize(context, inputUri)
        val finalSize = outputFile.length()
        println("RESULTADO COMPRESIÓN: ${originalSize / 1024 / 1024}MB -> ${finalSize / 1024 / 1024}MB")
        
        File(Uri.fromFile(outputFile))
    } catch (e: Exception) {
        println("COMPRESIÓN FALLIDA: ${e.message}. Subiendo original por seguridad.")
        File(inputUri)
    }
}

private fun getUriSize(context: android.content.Context, uri: Uri): Long {
    return try {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
    } catch (e: Exception) {
        0L
    }
}
