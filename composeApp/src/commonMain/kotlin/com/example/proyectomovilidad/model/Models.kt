package com.example.proyectomovilidad.model

import kotlinx.datetime.LocalDate

/**
 * Escala de valoración ALP (Assessment of Learning Process)
 * Se evalúa mensualmente para marcar la fase actual.
 */
data class AlpAssessment(
    val date: LocalDate,
    val phase: AlpPhase,
    val observations: String = ""
)

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
data class VideoUpload(
    val id: String,
    val date: LocalDate,
    val goalId: String, // Vinculado a un objetivo GAS
    val videoUrl: String,
    val durationSeconds: Int
)

/**
 * Documento de compromiso de préstamo.
 */
data class LoanCommitment(
    val parentName: String,
    val childName: String,
    val date: LocalDate,
    val deviceName: String,
    val signatureUrl: String? = null
)
