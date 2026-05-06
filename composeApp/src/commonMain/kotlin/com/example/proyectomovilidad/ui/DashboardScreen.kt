package com.example.proyectomovilidad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String?,
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
                    text = "Hola${if (userName != null) ", $userName" else ""}, bienvenido 👋",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Gestión de Atención Temprana",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                DashboardCard(
                    title = "Documento de Compromiso",
                    description = "Firma inicial del préstamo del dispositivo.",
                    icon = Icons.Default.Description,
                    onClick = onNavigateToLoan,
                    accentColor = Color(0xFF546E7A)
                )
            }

            item {
                DashboardCard(
                    title = "Escala ALP (Mensual)",
                    description = "Registra la fase de aprendizaje actual.",
                    icon = Icons.Default.Assessment,
                    onClick = onNavigateToAlp,
                    status = "Pendiente",
                    accentColor = Color(0xFF1976D2)
                )
            }

            item {
                DashboardCard(
                    title = "Objetivos GAS (Quincenal)",
                    description = "Evalúa y modifica los objetivos de entrenamiento.",
                    icon = Icons.Default.TrackChanges,
                    onClick = onNavigateToGas,
                    accentColor = Color(0xFFF57C00)
                )
            }

            item {
                DashboardCard(
                    title = "Subir Video",
                    description = "Sube un video de 2 min del entorno natural.",
                    icon = Icons.Default.VideoLibrary,
                    onClick = onNavigateToVideo,
                    accentColor = Color(0xFF388E3C)
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
    status: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = accentColor
                    )
                }
            }
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}
