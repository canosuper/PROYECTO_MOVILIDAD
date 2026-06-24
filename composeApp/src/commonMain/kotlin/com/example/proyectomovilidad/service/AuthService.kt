package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import com.example.proyectomovilidad.model.Patient
import com.example.proyectomovilidad.model.UserFisio
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

sealed class AuthResult {
    data class PatientSuccess(val patient: Patient) : AuthResult()
    data class FisioSuccess(val fisio: UserFisio) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthService {
    private val database = Firebase.database("https://proyecto-movilidad-18726-default-rtdb.europe-west1.firebasedatabase.app")

    /**
     * Login para pacientes mediante PIN
     */
    suspend fun loginWithPin(pin: String): AuthResult {
        return try {
            val cleanPin = pin.trim()
            println("--- INICIANDO BÚSQUEDA DE PIN: $cleanPin ---")
            
            val snapshot = withTimeout(10.seconds) {
                database.reference("pacientes").valueEvents.first()
            }
            val value = snapshot.value
            
            if (value == null) return AuthResult.Error("No hay pacientes registrados en el sistema")
            
            var foundPatient: Patient? = null

            // Función unificada para procesar cada entrada (sea clave o campo interno)
            fun processEntry(id: String, data: Any?) {
                if (foundPatient != null) return
                
                val key = id.substringBefore('.').trim()
                val map = data as? Map<*, *>
                val internalPin = map?.get("pin")?.toString()?.substringBefore('.')?.trim()
                
                println("Comparando PIN buscado '$cleanPin' con Clave '$key' y PIN Interno '$internalPin'")

                if (key == cleanPin || internalPin == cleanPin) {
                    println("¡COINCIDENCIA ENCONTRADA!")
                    val nombre = map?.get("nombre")?.toString() ?: data?.toString() ?: "Paciente $key"
                    foundPatient = Patient(
                        id = key,
                        nombre = nombre,
                        pin = cleanPin,
                        sedeId = map?.get("sedeId")?.toString() ?: ""
                    )
                }
            }

            when (value) {
                is Map<*, *> -> {
                    for (entry in value) {
                        processEntry(entry.key.toString(), entry.value)
                        if (foundPatient != null) break
                    }
                }
                is List<*> -> {
                    for (i in value.indices) {
                        if (value[i] != null) {
                            processEntry(i.toString(), value[i])
                        }
                        if (foundPatient != null) break
                    }
                }
            }

            if (foundPatient != null) {
                println("Login exitoso: ${foundPatient?.nombre}")
                AuthResult.PatientSuccess(foundPatient!!)
            } else {
                println("Error: No se encontró el PIN $cleanPin en la tabla pacientes")
                AuthResult.Error("El usuario con este PIN no existe")
            }
        } catch (e: Exception) {
            println("Error crítico en loginWithPin: ${e.message}")
            AuthResult.Error("Error de conexión: ${e.message}")
        }
    }

    /**
     * Login para fisios mediante Handle y Password
     */
    suspend fun loginAsFisio(handle: String, pass: String): AuthResult {
        return try {
            val snapshot = withTimeout(10.seconds) {
                database.reference("usuarios").valueEvents.first()
            }
            val usuariosMap = snapshot.value as? Map<String, Any> ?: return AuthResult.Error("Error al acceder a usuarios")

            var foundFisio: UserFisio? = null

            for ((id, data) in usuariosMap) {
                val map = data as? Map<String, Any>
                if (map?.get("handle")?.toString() == handle && map["password"]?.toString() == pass) {
                    foundFisio = UserFisio(
                        id = id,
                        handle = handle,
                        nombre = map["nombre"] as? String ?: "Fisioterapeuta",
                        rol = map["rol"] as? String ?: "fisio"
                    )
                    break
                }
            }

            if (foundFisio != null) {
                AuthResult.FisioSuccess(foundFisio)
            } else {
                AuthResult.Error("Usuario o contraseña incorrectos")
            }
        } catch (e: Exception) {
            AuthResult.Error("Error: ${e.message}")
        }
    }
}
