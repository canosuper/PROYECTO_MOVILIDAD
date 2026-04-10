package com.example.proyectomovilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovilidad.model.VideoUpload
import com.example.proyectomovilidad.service.VideoStorageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock as KtClock

class VideoViewModel : ViewModel() {
    private val storageService = VideoStorageService()
    
    private val _videos = MutableStateFlow<List<VideoUpload>>(emptyList())
    val videos: StateFlow<List<VideoUpload>> = _videos.asStateFlow()

    private val _isUploadingGlobal = MutableStateFlow(false)
    val isUploadingGlobal: StateFlow<Boolean> = _isUploadingGlobal.asStateFlow()

    fun uploadVideo(localUri: String, durationSeconds: Int, userId: String = "user_test_123") {
        viewModelScope.launch {
            _isUploadingGlobal.value = true
            
            // 1. Crear un registro temporal para mostrar en la UI
            val now = KtClock.System.now()
            val tempId = now.toEpochMilliseconds().toString()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            val tempVideo = VideoUpload(
                id = tempId,
                date = today,
                goalId = "GAS_TEMP", // Se vinculará después
                videoUrl = "",
                durationSeconds = durationSeconds,
                isUploading = true
            )
            
            _videos.value = _videos.value + tempVideo

            try {
                // 2. Subir a Storage
                val downloadUrl = storageService.uploadVideo(localUri, userId)
                
                // 3. Actualizar el registro con la URL real
                _videos.value = _videos.value.map { 
                    if (it.id == tempId) it.copy(videoUrl = downloadUrl, isUploading = false) 
                    else it 
                }
            } catch (e: Exception) {
                // Manejar error de subida
                _videos.value = _videos.value.filter { it.id != tempId }
                // Aquí podrías emitir un error a la UI
            } finally {
                _isUploadingGlobal.value = false
            }
        }
    }
}
