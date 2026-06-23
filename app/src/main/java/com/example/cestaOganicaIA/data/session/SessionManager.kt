package com.example.cestaOganicaIA.data.session

import com.example.cestaOganicaIA.data.model.Credential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _currentUserFlow = MutableStateFlow<Credential?>(null)
    val currentUserFlow: StateFlow<Credential?> = _currentUserFlow.asStateFlow()

    /** Acceso directo no reactivo, para usar dentro de funciones suspend o lógica fuera de Compose. */
    val currentUser: Credential? get() = _currentUserFlow.value

    fun login(user: Credential) {
        _currentUserFlow.value = user
    }

    fun logout() {
        _currentUserFlow.value = null
    }

    fun updateCurrent(user: Credential) {
        _currentUserFlow.value = user
    }

    val isLoggedIn: Boolean get() = currentUser != null
    val isGuest: Boolean get() = currentUser?.uid == "INVITADO"
    val isAdmin: Boolean get() = currentUser?.rol == "admin"
}