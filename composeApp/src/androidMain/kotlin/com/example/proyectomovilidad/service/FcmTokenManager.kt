package com.example.proyectomovilidad.service

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FcmTokenManager {
    suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            println("Error obteniendo token FCM: ${e.message}")
            null
        }
    }
}
