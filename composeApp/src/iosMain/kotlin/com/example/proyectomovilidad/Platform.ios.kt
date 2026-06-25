package com.example.proyectomovilidad

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual suspend fun getFcmToken(): String? {
    // Pendiente implementar con el SDK de Firebase para iOS
    return null
}