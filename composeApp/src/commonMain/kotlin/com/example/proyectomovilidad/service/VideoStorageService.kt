package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import dev.gitlive.firebase.storage.File
import kotlinx.datetime.Clock

// Esta función puente permitirá a cada plataforma crear el archivo de Firebase correctamente
expect fun createFirebaseFile(uri: String): File

class VideoStorageService {
    private val storage = Firebase.storage

    suspend fun uploadVideo(localUri: String, userId: String): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val fileName = "video_${timestamp}.mp4"
        val storageRef = storage.reference("videos/$userId/$fileName")
        
        // Usamos la implementación nativa de cada plataforma
        val fileToUpload = createFirebaseFile(localUri)
        
        // Realizamos la subida
        storageRef.putFile(fileToUpload)
        
        // Devolvemos la URL de descarga
        return storageRef.getDownloadUrl()
    }
}
