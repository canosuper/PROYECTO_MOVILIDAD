package com.example.proyectomovilidad.model

import kotlinx.serialization.Serializable

@Serializable
data class Patient(
    val id: String = "",
    val nombre: String = "",
    val pin: String = "",      // Login rápido
    val clave: String = "",    // Password opcional/alternativo
    val sedeId: String = "",   // Referencia a Sede
    val movimiento: String = "",
    val sesiones: Map<String, Session> = emptyMap(),
    val videos_entrenamiento: Map<String, VideoUpload> = emptyMap()
)

@Serializable
data class Session(
    val fecha: String = "",
    val observaciones: String = ""
)
