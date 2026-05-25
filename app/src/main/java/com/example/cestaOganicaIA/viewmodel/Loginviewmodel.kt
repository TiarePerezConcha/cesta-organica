package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cestaOganicaIA.ui.login.LoginUiState

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun setError(message: String?) {
        uiState = uiState.copy(error = message, isLoading = false)
    }

    fun setLoading(loading: Boolean) {
        uiState = uiState.copy(isLoading = loading)
    }
}
