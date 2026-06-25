package com.example.proyectomovilidad.model

import kotlinx.serialization.Serializable

@Serializable
data class DocumentInfo(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val url: String = ""
)
