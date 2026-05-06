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
        // Al iniciar, cargamos los vídeos y el perfil del usuario de prueba
        val testUserId = "user_test_123"
        loadVideos(testUserId)
        loadUserProfile(testUserId)
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            val name = databaseService.fetchUserName(userId)
            _userName.value = name
        }
    }

    fun loadVideos(userId: String) {
        viewModelScope.launch {
            val history = databaseService.fetchVideos(userId)
            _videos.value = history
        }
    }

    fun uploadVideo(localUri: String, durationSeconds: Int, userId: String = "user_test_123") {
        viewModelScope.launch {
            _isUploadingGlobal.value = true
            println("Iniciando subida de vídeo: $localUri")
            
            val now = kotlinx.datetime.Clock.System.now()
            val tempId = now.toEpochMilliseconds().toString()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            val tempVideo = VideoUpload(
                id = tempId,
                date = today,
                goalId = "GAS_TEMP",
                videoUrl = "",
                durationSeconds = durationSeconds,
                isUploading = true
            )
            
            _videos.value = _videos.value + tempVideo

            try {
                // 1. Subir el archivo a Storage con tiempo límite de 60s
                val downloadUrl = withTimeout(60.seconds) {
                    println("Subiendo a Storage...")
                    storageService.uploadVideo(localUri, userId)
                }
                println("Subida a Storage completada. URL: $downloadUrl")
                
                // 2. Crear el objeto final
                val finalVideo = tempVideo.copy(videoUrl = downloadUrl, isUploading = false)

                // 3. Guardar la referencia en Database con tiempo límite de 15s
                withTimeout(15.seconds) {
                    println("Guardando referencia en Database...")
                    databaseService.saveVideoReference(userId, finalVideo)
                }
                
                // 4. Actualizar la UI
                _videos.value = _videos.value.map { 
                    if (it.id == tempId) finalVideo else it 
                }
                println("Proceso de subida finalizado con éxito")
            } catch (e: Exception) {
                println("Error en el proceso de subida: ${e.message}")
                e.printStackTrace()
                // Eliminamos el vídeo temporal si falló
                _videos.value = _videos.value.filter { it.id != tempId }
            } finally {
                _isUploadingGlobal.value = false
            }
        }
    }
}
