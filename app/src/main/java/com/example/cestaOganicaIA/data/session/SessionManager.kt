package com.example.cestaOganicaIA.data.session

import com.example.cestaOganicaIA.data.model.Credential

object SessionManager {
    var currentUser: Credential? = null

    fun login(user: Credential) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun updateCurrent(user: Credential) {
        currentUser = user
    }

    val isLoggedIn: Boolean get() = currentUser != null
    val isGuest: Boolean get() = currentUser?.uid == "INVITADO"
    val isAdmin: Boolean get() = currentUser?.rol == "admin"
}
