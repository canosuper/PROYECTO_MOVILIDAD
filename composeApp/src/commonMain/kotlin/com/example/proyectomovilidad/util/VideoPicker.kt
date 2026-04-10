package com.example.proyectomovilidad.util

import androidx.compose.runtime.Composable

/**
 * Interface común para seleccionar o grabar videos en Android e iOS.
 */
@Composable
expect fun rememberVideoPicker(
    onVideoSelected: (uri: String, durationMs: Long) -> Unit,
    onError: (String) -> Unit
): VideoPickerLauncher

interface VideoPickerLauncher {
    fun launchGallery()
    fun launchCamera()
}
