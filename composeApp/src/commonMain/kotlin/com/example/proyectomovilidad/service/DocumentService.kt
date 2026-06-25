package com.example.proyectomovilidad.service

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import com.example.proyectomovilidad.model.DocumentInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

class DocumentService {
    private val database = Firebase.database("https://proyecto-movilidad-18726-default-rtdb.europe-west1.firebasedatabase.app")

    /**
     * Recupera la lista de documentos generales desde la raíz de la base de datos
     */
    suspend fun fetchDocuments(): List<DocumentInfo> {
        return try {
            println("Cargando documentos generales...")
            val snapshot = withTimeout(10.seconds) {
                database.reference("documentos_generales").valueEvents.first()
            }
            val value = snapshot.value
            
            if (value == null) return emptyList()

            val docsList = mutableListOf<DocumentInfo>()
            
            val items = when (value) {
                is Map<*, *> -> value
                else -> emptyMap<Any, Any>()
            }

            items.forEach { (id, data) ->
                (data as? Map<*, *>)?.let { map ->
                    docsList.add(
                        DocumentInfo(
                            id = id.toString(),
                            titulo = map["titulo"]?.toString() ?: "Sin título",
                            descripcion = map["descripcion"]?.toString() ?: "",
                            url = map["url"]?.toString() ?: ""
                        )
                    )
                }
            }
            
            println("Total documentos cargados: ${docsList.size}")
            docsList
        } catch (e: Exception) {
            println("Error al cargar documentos: ${e.message}")
            emptyList()
        }
    }
}
