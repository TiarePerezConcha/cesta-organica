package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Credential

class AuthRepository(
    private val validCredential: Credential = Credential.Admin
) {

    fun login(usernameOrEmail: String, password: String): Boolean {

        val userMatch = UserRepository.all().any {
            (it.usuario.equals(usernameOrEmail, ignoreCase = true) ||
                    it.correo.equals(usernameOrEmail, ignoreCase = true)) &&
                    it.password == password
        }


        val adminMatch = (
                usernameOrEmail.equals(validCredential.usuario, ignoreCase = true) &&
                        password == validCredential.password
                )


        return userMatch || adminMatch
    }
}
