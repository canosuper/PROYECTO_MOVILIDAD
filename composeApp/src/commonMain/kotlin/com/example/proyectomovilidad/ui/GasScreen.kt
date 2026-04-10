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
import com.example.proyectomovilidad.model.GasGoal
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasScreen(onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var goals by remember { mutableStateOf(listOf<GasGoal>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Objetivos GAS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo objetivo")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay objetivos registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = goal.description,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Nivel Inicial: ${goal.initialLevel}")
                            Text("Nivel Actual: ${goal.currentLevel}")
                            
                            LinearProgressIndicator(
                                progress = { (goal.currentLevel + 2) / 4f },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            )
                            
                            Text(
                                text = "Última actualización: ${goal.lastUpdate}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            var description by remember { mutableStateOf("") }
            var initialLevel by remember { mutableStateOf(0) }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Nuevo Objetivo GAS") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text("Nivel Inicial (-2 a +2):")
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            (-2..2).forEach { level ->
                                FilterChip(
                                    selected = initialLevel == level,
                                    onClick = { initialLevel = level },
                                    label = { Text(level.toString()) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val now = Clock.System.now()
                        val newGoal = GasGoal(
                            id = now.toEpochMilliseconds().toString(),
                            description = description,
                            initialLevel = initialLevel,
                            currentLevel = initialLevel,
                            lastUpdate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
                        )
                        goals = goals + newGoal
                        showDialog = false
                    }) {
                        Text("Añadir")
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
