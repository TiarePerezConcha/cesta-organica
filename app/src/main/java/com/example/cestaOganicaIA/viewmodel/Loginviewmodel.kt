package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.repository.AuthRepository
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    data class UiState(
        val username: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val loginSuccess: Boolean = false
    )

    var uiState by mutableStateOf(UiState())
        private set

    fun onUsernameChange(v: String) { uiState = uiState.copy(username = v, error = null) }
    fun onPasswordChange(v: String) { uiState = uiState.copy(password = v, error = null) }
    fun setError(msg: String?)      { uiState = uiState.copy(error = msg) }

    fun login() {
        val email = uiState.username.trim()
        val pass = uiState.password.trim()

        if (email.isBlank() || pass.isBlank()) {
            setError("Ingresa correo y contraseña")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            
            authRepo.login(email, pass).onSuccess { firebaseUser ->
                // Al loguear con éxito en Auth, buscamos sus datos adicionales en Firestore
                val profile = UserRepository.getProfile(firebaseUser.uid)
                if (profile != null) {
                    SessionManager.login(profile)
                    uiState = uiState.copy(loginSuccess = true)
                } else {
                    // Si no tiene perfil en Firestore (ej: primer login o error), creamos uno básico
                    val newProfile = Credential(
                        uid = firebaseUser.uid,
                        correo = firebaseUser.email ?: email,
                        nombre = firebaseUser.displayName ?: "Usuario"
                    )
                    UserRepository.saveProfile(newProfile)
                    SessionManager.login(newProfile)
                    uiState = uiState.copy(loginSuccess = true)
                }
            }.onFailure {
                uiState = uiState.copy(error = it.message ?: "Error al iniciar sesión")
            }
            uiState = uiState.copy(isLoading = false)
        }
    }
}
