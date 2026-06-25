package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.database.ServerValue
import com.example.proyectomovilidad.model.VideoUpload
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class VideoDatabaseService {
    private val database = Firebase.database("https://proyecto-movilidad-18726-default-rtdb.europe-west1.firebasedatabase.app")

    /**
     * Crea o actualiza el perfil del usuario en el nodo móvil para asegurar consistencia
     */
    suspend fun syncUserProfile(userId: String, name: String) {
        try {
            val perfilRef = database.reference("usuarios_movil/$userId/perfil")
            // Usamos un mapa para actualizar solo los campos necesarios sin borrar el resto (como el token)
            perfilRef.updateChildren(mapOf(
                "nombre" to name,
                "lastActivity" to ServerValue.TIMESTAMP 
            ))
            println("Perfil sincronizado en usuarios_movil para: $name")
        } catch (e: Exception) {
            println("Error sincronizando perfil: ${e.message}")
        }
    }

    suspend fun updateFcmToken(userId: String, token: String) {
        try {
            val tokenRef = database.reference("usuarios_movil/$userId/perfil/fcmToken")
            tokenRef.setValue(token)
            println("Token FCM actualizado para el usuario: $userId")
        } catch (e: Exception) {
            println("Error al actualizar token FCM: ${e.message}")
        }
    }

    suspend fun updateLastActivity(userId: String) {
        try {
            val activityRef = database.reference("usuarios_movil/$userId/perfil/lastActivity")
            activityRef.setValue(ServerValue.TIMESTAMP)
            println("Última actividad actualizada para: $userId")
        } catch (e: Exception) {
            println("Error al actualizar última actividad: ${e.message}")
        }
    }

    suspend fun saveVideoReference(userId: String, video: VideoUpload, userName: String? = null) {
        try {
            // Si nos pasan el nombre, aprovechamos para asegurar que el perfil existe en usuarios_movil
            userName?.let { syncUserProfile(userId, it) }

            println("Guardando referencia en DB: usuarios_movil/$userId/videos_entrenamiento/${video.id}")
            val videoRef = database.reference("usuarios_movil/$userId/videos_entrenamiento/${video.id}")
            val data = mapOf(
                "id" to video.id,
                "date" to video.date.toString(),
                "timestamp" to video.timestamp,
                "url" to video.videoUrl,
                "duration" to video.durationSeconds
            )
            videoRef.setValue(data)
            updateLastActivity(userId) // Cada vez que sube un vídeo, actualizamos actividad
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
                database.reference("usuarios_movil/$userId/videos_entrenamiento").valueEvents.first()
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
            println("Buscando nombre para: $userId en usuarios_movil")
            val snapshot = withTimeout(5.seconds) {
                database.reference("usuarios_movil/$userId").valueEvents.first()
            }
            val data = snapshot.value as? Map<String, Any>
            
            // Intento 1: usuarios_movil/ID/perfil/nombre
            // Intento 2: usuarios_movil/ID/nombre
            val name = (data?.get("perfil") as? Map<String, Any>)?.get("nombre") as? String
                ?: data?.get("nombre") as? String
            
            println("Nombre encontrado en DB: $name")
            name
        } catch (e: Exception) {
            println("Error al recuperar nombre de DB: ${e.message}")
            null
        }
    }

    suspend fun deleteVideoReference(userId: String, videoId: String) {
        try {
            val videoRef = database.reference("usuarios_movil/$userId/videos_entrenamiento/$videoId")
            println("Borrando referencia en DB: usuarios_movil/$userId/videos_entrenamiento/$videoId")
            videoRef.removeValue()
            println("Referencia de DB borrada con éxito")
        } catch (e: Exception) {
            println("Error al borrar referencia de DB: ${e.message}")
            throw e
        }
    }
}
