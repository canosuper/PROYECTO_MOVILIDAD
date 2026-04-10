package com.example.proyectomovilidad.service

import dev.gitlive.firebase.storage.File
import platform.Foundation.NSURL

actual fun createFirebaseFile(uri: String): File {
    return File(NSURL(string = uri))
}
