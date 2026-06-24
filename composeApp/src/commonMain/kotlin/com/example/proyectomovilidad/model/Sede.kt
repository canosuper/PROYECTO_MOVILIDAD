package com.example.proyectomovilidad.model

import kotlinx.serialization.Serializable

@Serializable
data class Sede(
    val id: String = "",
    val nombre: String = "",
    val poblacion: String = ""
)
