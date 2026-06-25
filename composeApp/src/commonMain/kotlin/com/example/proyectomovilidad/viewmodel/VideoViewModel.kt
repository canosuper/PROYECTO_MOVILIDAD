package com.example.proyectomovilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.service.VideoDatabaseService
import com.example.proyectomovilidad.service.VideoStorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class VideoViewModel : ViewModel() {
    private val storageService = VideoStorageService()
    private val databaseService = VideoDatabaseService()
    
    private val _videos = MutableStateFlow<List<VideoUpload>>(emptyList())
    val videos: StateFlow<List<VideoUpload>> = _videos.asStateFlow()

    private val _isUploadingGlobal = MutableStateFlow(false)
    val isUploadingGlobal: StateFlow<Boolean> = _isUploadingGlobal.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        // Ya no cargamos nada por defecto al iniciar
    }

    fun updateFCMToken(userId: String) {
        viewModelScope.launch {
            try {
                // Importamos la función que creamos en Platform.kt
                val token = com.example.proyectomovilidad.getFcmToken()
                if (token != null) {
                    databaseService.updateFcmToken(userId, token)
                    databaseService.updateLastActivity(userId) // Registra actividad al entrar
                }
            } catch (e: Exception) {
                println("Error al actualizar Token en login: ${e.message}")
            }
        }
    }

    fun loadUserProfile(userId: String, loginName: String? = null) {
        viewModelScope.launch {
            // Intentamos cargar el nombre de la base de datos móvil
            val nameFromDb = databaseService.fetchUserName(userId)
            // Si no está en la móvil pero tenemos el del login, usamos ese
            _userName.value = nameFromDb ?: loginName
        }
    }

    fun loadVideos(userId: String) {
        viewModelScope.launch {
            val history = databaseService.fetchVideos(userId)
            // Ordenar por timestamp descendente (más nuevo primero)
            _videos.value = history.sortedByDescending { it.timestamp }
        }
    }

    fun clearData() {
        _videos.value = emptyList()
        _userName.value = null
    }

    fun uploadVideo(localUri: String, durationSeconds: Int, userId: String) {
        viewModelScope.launch {
            _isUploadingGlobal.value = true
            println("Iniciando subida de vídeo: $localUri")
            
            val now = Clock.System.now()
            val tempId = now.toEpochMilliseconds().toString()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            val tempVideo = VideoUpload(
                id = tempId,
                date = today,
                timestamp = now.toEpochMilliseconds(),
                goalId = "GAS_TEMP",
                videoUrl = "",
                durationSeconds = durationSeconds,
                isUploading = true
            )
            
            // Insertar al principio para que aparezca arriba inmediatamente
            _videos.value = listOf(tempVideo) + _videos.value

            try {
                // 1. Subir el archivo a Storage con tiempo límite de 60s
                val downloadUrl = withTimeout(60.seconds) {
                    storageService.uploadVideo(localUri, userId, tempId)
                }
                
                // 2. Crear el objeto final
                val finalVideo = tempVideo.copy(videoUrl = downloadUrl, isUploading = false)

                // 3. Guardar la referencia Y sincronizar perfil si es necesario
                withTimeout(15.seconds) {
                    databaseService.saveVideoReference(userId, finalVideo, _userName.value)
                }
                
                // 4. Actualizar la UI
                _videos.value = _videos.value.map { 
                    if (it.id == tempId) finalVideo else it 
                }
            } catch (e: Exception) {
                println("Error en subida: ${e.message}")
                _videos.value = _videos.value.filter { it.id != tempId }
            } finally {
                _isUploadingGlobal.value = false
            }
        }
    }

    fun deleteVideo(video: VideoUpload, userId: String) {
        viewModelScope.launch {
            try {
                // 1. Borrar de Storage físico
                storageService.deleteVideo(userId, video.id)
                
                // 2. Borrar referencia en Database
                databaseService.deleteVideoReference(userId, video.id)
                
                // 3. Actualizar UI
                _videos.value = _videos.value.filter { it.id != video.id }
                println("Video eliminado con éxito de todos los sitios")
            } catch (e: Exception) {
                println("Error en el proceso de borrado: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
