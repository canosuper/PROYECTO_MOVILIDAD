package com.example.proyectomovilidad

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform