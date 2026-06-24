package com.example.proyectomovilidad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovilidad.service.AuthResult
import com.example.proyectomovilidad.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class SuccessPatient(val patientId: String, val name: String) : LoginState()
    data class SuccessFisio(val fisioId: String, val name: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val authService: AuthService = AuthService()) : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(input: String, password: String? = null) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            
            // Si el input es puramente numérico y de longitud 4-6, probamos como PIN
            val isPin = input.all { it.isDigit() } && (input.length in 4..6) && password.isNullOrBlank()

            val result = if (isPin) {
                authService.loginWithPin(input)
            } else if (!password.isNullOrBlank()) {
                authService.loginAsFisio(input, password)
            } else {
                // Caso especial para tu usuario de prueba actual
                if (input == "user_test_123") {
                    _state.value = LoginState.SuccessPatient("user_test_123", "Antonio")
                    return@launch
                }
                AuthResult.Error("Introduce PIN o Usuario/Contraseña")
            }

            when (result) {
                is AuthResult.PatientSuccess -> {
                    _state.value = LoginState.SuccessPatient(result.patient.id, result.patient.nombre)
                }
                is AuthResult.FisioSuccess -> {
                    _state.value = LoginState.SuccessFisio(result.fisio.id, result.fisio.nombre)
                }
                is AuthResult.Error -> {
                    _state.value = LoginState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }
}
