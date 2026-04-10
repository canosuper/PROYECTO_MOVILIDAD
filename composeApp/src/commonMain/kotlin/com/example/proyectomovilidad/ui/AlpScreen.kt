package com.example.proyectomovilidad.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectomovilidad.model.AlpAssessment
import com.example.proyectomovilidad.model.AlpPhase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlpScreen(onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var assessments by remember { mutableStateOf(listOf<AlpAssessment>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escala ALP") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva valoración")
            }
        }
    ) { padding ->
        if (assessments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay valoraciones registradas")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(assessments) { assessment ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Fecha: ${assessment.date}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Fase: ${assessment.phase.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (assessment.observations.isNotEmpty()) {
                                Text(
                                    text = assessment.observations,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            var selectedPhase by remember { mutableStateOf(AlpPhase.NO_CONTACT) }
            var observations by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nueva valoración ALP") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Selecciona la fase actual:")
                        AlpPhase.entries.forEach { phase ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = selectedPhase == phase,
                                    onClick = { selectedPhase = phase }
                                )
                                Text(
                                    text = phase.name.replace("_", " "),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        OutlinedTextField(
                            value = observations,
                            onValueChange = { observations = it },
                            label = { Text("Observaciones") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val now = Clock.System.now()
                        val newAssessment = AlpAssessment(
                            date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date,
                            phase = selectedPhase,
                            observations = observations
                        )
                        assessments = assessments + newAssessment
                        showDialog = false
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
