package com.example.proyectomovilidad.model

import kotlinx.datetime.LocalDate

import kotlinx.serialization.Serializable

/**
 * Escala de valoración ALP (Assessment of Learning Process)
 * Se evalúa mensualmente para marcar la fase actual.
 */
@Serializable
data class AlpAssessment(
    val date: LocalDate,
    val phase: AlpPhase,
    val observations: String = ""
)

@Serializable
enum class AlpPhase {
    NO_CONTACT,       // Sin contacto
    CONTACT,          // Contacto
    EXPLORATION,      // Exploración
    FUNCTIONAL_USE,   // Uso funcional
    MASTERY           // Dominio
}

/**
 * Escala GAS (Goal Attainment Scaling)
 * Documento abierto para evaluar objetivos de entrenamiento.
 */
@Serializable
data class GasGoal(
    val id: String,
    val description: String,
    val initialLevel: Int, // -2 a +2
    val currentLevel: Int,
    val lastUpdate: LocalDate
)

/**
 * Registro de video subido por la familia.
 */
@Serializable
data class VideoUpload(
    val id: String,
    val date: LocalDate,
    val timestamp: Long = 0L, // Añadido para ordenación precisa y hora
    val goalId: String, // Vinculado a un objetivo GAS
    val videoUrl: String,
    val durationSeconds: Int,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f
)

/**
 * Documento de compromiso de préstamo.
 */
@Serializable
data class LoanCommitment(
    val parentName: String,
    val childName: String,
    val date: LocalDate,
    val deviceName: String,
    val signatureUrl: String? = null
)
