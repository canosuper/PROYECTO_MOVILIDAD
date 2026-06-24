package com.example.proyectomovilidad.model

import kotlinx.serialization.Serializable

@Serializable
data class UserFisio(
    val id: String = "",
    val handle: String = "",
    val password: String = "", // Encriptado en la web (AES/Base64)
    val nombre: String = "",
    val rol: String = "fisio" // fisio, admin
)
