package com.example.proyectomovilidad.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyectomovilidad.viewmodel.LoginState
import com.example.proyectomovilidad.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (id: String, name: String, isFisio: Boolean) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Si el estado cambia a éxito, navegamos
    LaunchedEffect(state) {
        when (val s = state) {
            is LoginState.SuccessPatient -> onLoginSuccess(s.patientId, s.name, false)
            is LoginState.SuccessFisio -> onLoginSuccess(s.fisioId, s.name, true)
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Introduce tu PIN (Pacientes) o Usuario (Fisios)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("PIN o Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (identifier.all { it.isDigit() }) KeyboardType.Number else KeyboardType.Text
            )
        )

        // Solo mostramos el campo password si no parece ser un PIN
        val isLikelyPin = identifier.length in 4..6 && identifier.all { it.isDigit() }
        
        if (!isLikelyPin && identifier.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Ver contraseña"
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state is LoginState.Error) {
            Text(
                text = (state as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(identifier, if (isLikelyPin) null else password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state !is LoginState.Loading && identifier.isNotEmpty()
        ) {
            if (state is LoginState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar")
            }
        }
        
        // Botón rápido para desarrollo
        TextButton(
            onClick = { viewModel.login("user_test_123") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Entrar como Usuario de Prueba (Dev)")
        }
    }
}
