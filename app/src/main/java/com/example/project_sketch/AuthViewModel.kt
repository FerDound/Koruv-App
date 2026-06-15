package com.example.project_sketch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class Pregunta(
    val texto: String,
    val opciones: List<String>
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    var email by mutableStateOf("")
    var numeroControl by mutableStateOf("")
    var password by mutableStateOf("")
    var username by mutableStateOf("")
    var nombre by mutableStateOf("")
    var apellidoPaterno by mutableStateOf("")
    var apellidoMaterno by mutableStateOf("")
    var nombrepublico by mutableStateOf("")
    var carrera by mutableStateOf("")
    var semestre by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when {
                email.isBlank() -> { errorMessage = "Ingresa un correo"; return@launch }
                password.isBlank() -> { errorMessage = "Ingresa una contraseña"; return@launch }
            }
            isLoading = true
            errorMessage = null
            repository.login(email, password)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = traducirError(it.message) }
            isLoading = false
        }
    }
    fun validarSingUp(onValido: () -> Unit) {
        viewModelScope.launch {
            when {
                username.isBlank() -> { errorMessage = "Ingresa un nombre de usuario"; return@launch }
                email.isBlank() -> { errorMessage = "Ingresa un correo"; return@launch }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    errorMessage = "El formato del correo no es válido"; return@launch
                }
                !email.endsWith("@cbtis041.edu.mx") -> {
                    errorMessage = "Solo se permiten correos @cbtis041.edu.mx"; return@launch
                }
                username.length > 15 -> { errorMessage = "El usuario no puede tener más de 15 caracteres"; return@launch }
                password.isBlank() -> { errorMessage = "Ingresa una contraseña"; return@launch }
                password.length < 6 -> { errorMessage = "La contraseña debe tener al menos 6 caracteres"; return@launch }
            }
            errorMessage = null
            onValido()
        }
    }
    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when {
                username.isBlank() -> { errorMessage = "Ingresa un nombre de usuario"; return@launch }
                email.isBlank() -> { errorMessage = "Ingresa un correo"; return@launch }
                password.isBlank() -> { errorMessage = "Ingresa una contraseña"; return@launch }
                password.length < 6 -> { errorMessage = "La contraseña debe tener al menos 6 caracteres"; return@launch }
                nombrepublico.isBlank() -> { errorMessage = "Ingresa tu nombre público"; return@launch }
                carrera.isBlank() -> { errorMessage = "Selecciona tu especialidad"; return@launch }
                semestre.isBlank() -> { errorMessage = "Selecciona tu grupo"; return@launch }
            }
            isLoading = true
            errorMessage = null
            repository.register(email.trim(), password.trim(), username.trim(), nombre.trim(), apellidoPaterno.trim(), apellidoMaterno.trim(), numeroControl.trim(), nombrepublico.trim(), carrera.trim(), semestre.trim())
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = traducirError(it.message) }
            isLoading = false
        }
    }

    private fun traducirError(mensaje: String?): String {
        return when {
            mensaje == null -> "Ocurrió un error inesperado"
            "email address is already in use" in mensaje -> "Este correo ya está registrado"
            "email address is badly formatted" in mensaje -> "El formato del correo no es válido"
            "no user record" in mensaje -> "No existe una cuenta con este correo"
            "password is invalid" in mensaje || "incorrect" in mensaje -> "Contraseña incorrecta"
            "too many requests" in mensaje -> "Demasiados intentos, intenta más tarde"
            "network error" in mensaje -> "Sin conexión a internet"
            "user has been disabled" in mensaje -> "Esta cuenta ha sido deshabilitada"
            "weak password" in mensaje -> "La contraseña es muy débil"
            else -> "Ocurrió un error, intenta de nuevo"
        }
    }
}