package com.example.proyectomovilidad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToLoan: () -> Unit,
    onNavigateToAlp: () -> Unit,
    onNavigateToGas: () -> Unit,
    onNavigateToVideo: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Proyecto Movilidad AT") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Tareas Pendientes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                DashboardCard(
                    title = "Documento de Compromiso",
                    description = "Firma inicial del préstamo del dispositivo.",
                    icon = Icons.Default.Description,
                    onClick = onNavigateToLoan
                )
            }

            item {
                DashboardCard(
                    title = "Escala ALP (Mensual)",
                    description = "Registra la fase de aprendizaje actual.",
                    icon = Icons.Default.Assessment,
                    onClick = onNavigateToAlp,
                    status = "Pendiente"
                )
            }

            item {
                DashboardCard(
                    title = "Objetivos GAS (Quincenal)",
                    description = "Evalúa y modifica los objetivos de entrenamiento.",
                    icon = Icons.Default.TrackChanges,
                    onClick = onNavigateToGas
                )
            }

            item {
                DashboardCard(
                    title = "Subir Video",
                    description = "Sube un video de 2 min del entorno natural.",
                    icon = Icons.Default.VideoLibrary,
                    onClick = onNavigateToVideo
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    status: String? = null
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
                if (status != null) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}
