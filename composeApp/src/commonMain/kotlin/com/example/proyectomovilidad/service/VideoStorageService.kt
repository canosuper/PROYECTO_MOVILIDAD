package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

expect suspend fun createFirebaseFile(uri: String): File

sealed class UploadStatus {
    data class Progressing(val percent: Float) : UploadStatus()
    data class Success(val downloadUrl: String) : UploadStatus()
}

class VideoStorageService {
    private val storage = Firebase.storage("gs://proyecto-movilidad-18726.firebasestorage.app")

    fun uploadVideoWithProgress(localUri: String, userId: String, videoId: String): Flow<UploadStatus> = flow {
        val fileName = "video_${videoId}.mp4"
        val storageRef = storage.reference("videos/$userId/$fileName")
        
        val fileToUpload = createFirebaseFile(localUri)
        
        // Usamos putFileResumable para obtener el flujo de progreso
        val task = storageRef.putFileResumable(fileToUpload)
        
        task.collect { progress ->
            val transferred = progress.bytesTransferred.toDouble()
            val total = progress.totalByteCount.toDouble()
            val ratio = if (total > 0.0) (transferred / total).toFloat() else 0f
            emit(UploadStatus.Progressing(ratio))
        }
        
        val downloadUrl = storageRef.getDownloadUrl()
        emit(UploadStatus.Success(downloadUrl))
    }

    suspend fun deleteVideo(userId: String, videoId: String) {
        try {
            val fileName = "video_${videoId}.mp4"
            val storageRef = storage.reference("videos/$userId/$fileName")
            storageRef.delete()
        } catch (e: Exception) {
            println("Aviso: No se pudo borrar el archivo físico: ${e.message}")
        }
    }
}
