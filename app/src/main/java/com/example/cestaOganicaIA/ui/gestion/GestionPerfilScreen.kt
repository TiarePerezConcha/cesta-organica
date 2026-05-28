package com.example.cestaOganicaIA.ui.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import kotlinx.coroutines.launch

@Composable
fun GestionPerfilScreen(navController: NavController, userRepository: UserRepository) {
    val current = SessionManager.currentUser
    val scope = rememberCoroutineScope()

    if (current == null || SessionManager.isGuest) {
        LaunchedEffect(Unit) {
            navController.navigate(AppRoutes.REGISTRO) { popUpTo(0) { inclusive = true } }
        }
        return
    }

    var nombre by remember { mutableStateOf(current.nombre) }
    var telefono by remember { mutableStateOf(current.telefono) }
    var direccion by remember { mutableStateOf(current.direccion) }
    var generalErr by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showOk by remember { mutableStateOf(false) }

    HuertoScaffold(
        titulo = "Mi Perfil",
        navController = navController
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = current.usuario, onValueChange = {}, label = { Text("Usuario") }, enabled = false, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = current.correo, onValueChange = {}, label = { Text("Correo") }, enabled = false, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = telefono, onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 9) telefono = it },
                label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

            if (generalErr != null) Text(generalErr!!, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        userRepository.updateProfile(current.uid, nombre, telefono, direccion).onSuccess {
                            SessionManager.updateCurrent(it)
                            showOk = true
                        }.onFailure { generalErr = it.message }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("Guardar Cambios")
            }
        }
        if (showOk) {
            AlertDialog(
                onDismissRequest = { showOk = false },
                title = { Text("Éxito") },
                text = { Text("Perfil actualizado correctamente") },
                confirmButton = { TextButton(onClick = { showOk = false }) { Text("OK") } }
            )
        }
    }
}
