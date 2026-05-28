package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.login.LoginUiState
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun login() {
        if (uiState.username.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(error = "Completa todos los campos")
            return
        }

        uiState = uiState.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            val result = repository.login(uiState.username, uiState.password)
            result.fold(
                onSuccess = { user ->
                    SessionManager.login(user)
                    uiState = uiState.copy(isLoading = false, loginSuccess = true)
                },
                onFailure = { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message ?: "Error al iniciar sesión")
                }
            )
        }
    }

    fun setError(message: String?) {
        uiState = uiState.copy(error = message, isLoading = false)
    }

    fun setLoading(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(repository) as T
    }
}
