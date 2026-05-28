package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.session.SessionManager

class AuthRepository(private val userRepository: UserRepository) {

    /**
     * Intenta iniciar sesión comparando con la base de datos local de usuarios.
     */
    suspend fun login(usernameOrEmail: String, password: String): Result<Credential> {
        val result = userRepository.login(usernameOrEmail, password)
        return result.onSuccess { userMatch ->
            SessionManager.login(userMatch)
        }
    }
}
