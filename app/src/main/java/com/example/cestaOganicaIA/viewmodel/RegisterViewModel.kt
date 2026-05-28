package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    
    var isLoading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf<String?>(null)
        private set
    var registrationSuccess by mutableStateOf(false)
        private set

    fun register(nombre: String, correo: String, usuario: String, telefono: String, direccion: String, clave: String) {
        viewModelScope.launch {
            isLoading = true
            errorMsg = null
            
            val newUser = Credential(
                nombre = nombre,
                correo = correo,
                usuario = usuario,
                telefono = telefono,
                direccion = direccion,
                password = clave
            )

            val result = repository.register(newUser)
            
            result.onSuccess {
                registrationSuccess = true
            }.onFailure {
                errorMsg = it.message ?: "Error al crear cuenta"
            }

            isLoading = false
        }
    }

    fun resetError() { errorMsg = null }

    class Factory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RegisterViewModel(repository) as T
    }
}
