package com.example.proyectomovilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.service.VideoDatabaseService
import com.example.proyectomovilidad.service.VideoStorageService
import com.example.proyectomovilidad.service.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.time.Duration.Companion.seconds

class VideoViewModel : ViewModel() {
    private val storageService = VideoStorageService()
    private val databaseService = VideoDatabaseService()
    
    private val _videos = MutableStateFlow<List<VideoUpload>>(emptyList())
    val videos: StateFlow<List<VideoUpload>> = _videos.asStateFlow()

    private val _isUploadingGlobal = MutableStateFlow(false)
    val isUploadingGlobal: StateFlow<Boolean> = _isUploadingGlobal.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        // Ya no cargamos nada por defecto al iniciar
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun updateFCMToken(userId: String) {
        viewModelScope.launch {
            try {
                val token = com.example.proyectomovilidad.getFcmToken()
                if (token != null) {
                    databaseService.updateFcmToken(userId, token)
                    databaseService.updateLastActivity(userId)
                }
            } catch (e: Exception) {
                println("Error al actualizar Token en login: ${e.message}")
            }
        }
    }

    fun loadUserProfile(userId: String, loginName: String? = null) {
        viewModelScope.launch {
            val nameFromDb = databaseService.fetchUserName(userId)
            _userName.value = nameFromDb ?: loginName
        }
    }

    fun loadVideos(userId: String) {
        viewModelScope.launch {
            val history = databaseService.fetchVideos(userId)
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
            _uploadProgress.value = 0f
            _errorMessage.value = null
            
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
            
            _videos.value = listOf(tempVideo) + _videos.value

            try {
                // Timeout ajustado a 60 segundos por petición
                withTimeout(60.seconds) {
                    storageService.uploadVideoWithProgress(localUri, userId, tempId).collect { status ->
                        when (status) {
                            is UploadStatus.Progressing -> {
                                _uploadProgress.value = status.percent
                            }
                            is UploadStatus.Success -> {
                                val finalVideo = tempVideo.copy(videoUrl = status.downloadUrl, isUploading = false)
                                
                                // Guardar referencia en DB
                                databaseService.saveVideoReference(userId, finalVideo, _userName.value)
                                
                                // Actualizar UI
                                _videos.value = _videos.value.map { 
                                    if (it.id == tempId) finalVideo else it 
                                }
                            }
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _errorMessage.value = "Tiempo de espera agotado. La subida tardó más de 60 segundos."
                _videos.value = _videos.value.filter { it.id != tempId }
            } catch (e: Exception) {
                println("Error en subida: ${e.message}")
                _errorMessage.value = "Error al subir el vídeo: ${e.message}"
                _videos.value = _videos.value.filter { it.id != tempId }
            } finally {
                _isUploadingGlobal.value = false
                _uploadProgress.value = 0f
            }
        }
    }

    fun deleteVideo(video: VideoUpload, userId: String) {
        viewModelScope.launch {
            try {
                storageService.deleteVideo(userId, video.id)
                databaseService.deleteVideoReference(userId, video.id)
                _videos.value = _videos.value.filter { it.id != video.id }
            } catch (e: Exception) {
                println("Error en el proceso de borrado: ${e.message}")
            }
        }
    }
}
