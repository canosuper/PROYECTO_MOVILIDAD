package com.example.proyectomovilidad.service

import android.net.Uri
import dev.gitlive.firebase.storage.File

actual fun createFirebaseFile(uri: String): File {
    return File(Uri.parse(uri))
}
