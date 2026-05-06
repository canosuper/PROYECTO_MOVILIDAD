package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.File

// Esta función puente permitirá a cada plataforma crear el archivo de Firebase correctamente
expect fun createFirebaseFile(uri: String): File

class VideoStorageService {
    // Especificamos el bucket explícitamente como hicimos con la DB para evitar desvíos de región
    private val storage = Firebase.storage("gs://proyecto-movilidad-18726.firebasestorage.app")

    suspend fun uploadVideo(localUri: String, userId: String): String {
        try {
            val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            val fileName = "video_${timestamp}.mp4"
            println("Preparando subida a Storage: videos/$userId/$fileName")
            val storageRef = storage.reference("videos/$userId/$fileName")
            
            val fileToUpload = createFirebaseFile(localUri)
            
            println("Iniciando putFile para: $localUri")
            storageRef.putFile(fileToUpload)
            println("putFile completado con éxito")
            
            val downloadUrl = storageRef.getDownloadUrl()
            println("URL de descarga obtenida: $downloadUrl")
            return downloadUrl
        } catch (e: Exception) {
            println("Error específico en VideoStorageService: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
