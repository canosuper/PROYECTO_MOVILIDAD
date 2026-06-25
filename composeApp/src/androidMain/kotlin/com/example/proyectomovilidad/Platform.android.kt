package com.example.proyectomovilidad

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual suspend fun getFcmToken(): String? = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
    com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result, null)
        } else {
            continuation.resume(null, null)
        }
    }
}