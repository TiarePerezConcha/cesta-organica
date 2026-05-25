package com.example.cestaOganicaIA.data.session

import com.example.cestaOganicaIA.data.model.Credential

/**
 * Gestor de sesión global.
 * Centraliza la información del usuario autenticado en Firebase.
 */
object SessionManager {

    var currentUser: Credential? = null
        private set

    val isAdmin: Boolean
        get() = currentUser?.uid == Credential.Admin.uid ||
                currentUser?.usuario?.equals("admin", ignoreCase = true) == true

    val isGuest: Boolean
        get() = currentUser?.uid == "INVITADO"

    fun login(user: Credential) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun updateCurrent(updated: Credential) {
        if (currentUser?.uid == updated.uid) {
            currentUser = updated
        }
    }
}
