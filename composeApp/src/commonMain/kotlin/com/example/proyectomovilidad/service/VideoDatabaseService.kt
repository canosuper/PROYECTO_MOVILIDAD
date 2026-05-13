package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import com.example.proyectomovilidad.model.VideoUpload
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class VideoDatabaseService {
    private val database = Firebase.database("https://proyecto-movilidad-18726-default-rtdb.europe-west1.firebasedatabase.app")

    suspend fun saveVideoReference(userId: String, video: VideoUpload) {
        try {
            println("Guardando referencia en DB: usuarios/$userId/videos_entrenamiento/${video.id}")
            val videoRef = database.reference("usuarios/$userId/videos_entrenamiento/${video.id}")
            val data = mapOf(
                "id" to video.id,
                "date" to video.date.toString(),
                "timestamp" to video.timestamp,
                "url" to video.videoUrl,
                "duration" to video.durationSeconds
            )
            videoRef.setValue(data)
            println("Referencia guardada con éxito")
        } catch (e: Exception) {
            println("Error al guardar en DB: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun fetchVideos(userId: String): List<VideoUpload> {
        return try {
            println("Cargando vídeos para el usuario: $userId")
            val snapshot = withTimeout(10.seconds) {
                database.reference("usuarios/$userId/videos_entrenamiento").valueEvents.first()
            }
            val value = snapshot.value
            
            println("Datos recibidos de DB: $value")
            
            if (value == null) return emptyList()

            // Manejo más robusto del parseo (puede venir como Map o List)
            val videosList = mutableListOf<VideoUpload>()
            
            val items = when (value) {
                is Map<*, *> -> value.values
                is List<*> -> value.filterNotNull()
                else -> emptyList<Any>()
            }

            items.forEach { item ->
                (item as? Map<String, Any>)?.let { map ->
                    videosList.add(
                        VideoUpload(
                            id = map["id"] as? String ?: "",
                            date = LocalDate.parse(map["date"] as? String ?: ""),
                            timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L,
                            goalId = "GAS_TEMP",
                            videoUrl = map["url"] as? String ?: "",
                            durationSeconds = (map["duration"] as? Number)?.toInt() ?: 0
                        )
                    )
                }
            }
            
            println("Total vídeos cargados: ${videosList.size}")
            videosList
        } catch (e: Exception) {
            println("Error al cargar vídeos de DB: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchUserName(userId: String): String? {
        return try {
            val snapshot = withTimeout(5.seconds) {
                database.reference("usuarios/$userId/perfil/nombre").valueEvents.first()
            }
            snapshot.value as? String
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteVideoReference(userId: String, videoId: String) {
        try {
            val videoRef = database.reference("usuarios/$userId/videos_entrenamiento/$videoId")
            println("Borrando referencia en DB: usuarios/$userId/videos_entrenamiento/$videoId")
            videoRef.removeValue()
            println("Referencia de DB borrada con éxito")
        } catch (e: Exception) {
            println("Error al borrar referencia de DB: ${e.message}")
            throw e
        }
    }
}
