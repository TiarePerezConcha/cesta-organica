package com.example.cestaOganicaIA.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.RegisterViewModel

@Composable
fun RegistroScreen(navController: NavController, vm: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    var nombre    by remember { mutableStateOf("") }
    var correo    by remember { mutableStateOf("") }
    var usuario   by remember { mutableStateOf("") }
    var telefono  by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var clave     by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verClave  by remember { mutableStateOf(false) }

    val errorMsg = vm.errorMsg
    val isLoading = vm.isLoading

    if (vm.registrationSuccess) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Cuenta creada!") },
            text = { Text("Tu cuenta se creó correctamente en la nube. Ahora puedes iniciar sesión.") },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate(AppRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                }) { Text("Iniciar sesión") }
            }
        )
    }

    HuertoHogarTheme {
        Scaffold { inner ->
            Column(
                modifier = Modifier.padding(inner).fillMaxSize()
                    .verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Crear cuenta en Cesta Orgánica", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it; vm.resetError() },
                    label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correo, onValueChange = { correo = it; vm.resetError() },
                    label = { Text("Correo electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = usuario, onValueChange = { usuario = it; vm.resetError() },
                    label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it.filter { it.isDigit() }.take(9); vm.resetError() },
                    label = { Text("Teléfono (9 dígitos)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion, onValueChange = { direccion = it; vm.resetError() },
                    label = { Text("Dirección de entrega") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clave, onValueChange = { clave = it; vm.resetError() },
                    label = { Text("Contraseña") }, 
                    visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { verClave = !verClave }) {
                            Icon(if (verClave) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmar, onValueChange = { confirmar = it; vm.resetError() },
                    label = { Text("Confirmar contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = {
                        if (clave != confirmar) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (nombre.isBlank() || correo.isBlank() || clave.isBlank()) {
                            Toast.makeText(context, "Por favor completa los campos básicos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        vm.register(nombre, correo, usuario, telefono, direccion, clave)
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Crear cuenta")
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Ya tengo cuenta, iniciar sesión")
                }
            }
        }
    }
}
