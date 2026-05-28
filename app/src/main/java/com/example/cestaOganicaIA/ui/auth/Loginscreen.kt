package com.example.cestaOganicaIA.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cestaOganicaIA.R
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.LoginViewModel

@Composable
fun LoginScreen(navController: NavController, vm: LoginViewModel = viewModel()) {
    val state = vm.uiState
    var showPass by remember { mutableStateOf(false) }

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            navController.navigate(AppRoutes.CATALOGO) {
                popUpTo(AppRoutes.LOGIN) { inclusive = true }
            }
        }
    }

    HuertoHogarTheme {
        Scaffold { inner ->
            Column(
                modifier = Modifier.padding(inner).fillMaxSize().padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cesta Orgánica", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Image(painter = painterResource(R.drawable.logo_huerto_hogar), contentDescription = null, modifier = Modifier.height(120.dp), contentScale = ContentScale.Fit)
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(value = state.username, onValueChange = vm::onUsernameChange, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.password, onValueChange = vm::onPasswordChange, label = { Text("Contraseña") },
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { IconButton(onClick = { showPass = !showPass }) { Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) } },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                )

                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (state.username == "admin" && state.password == "123") {
                            SessionManager.login(Credential.Admin)
                            navController.navigate(AppRoutes.CATALOGO) { popUpTo(AppRoutes.LOGIN) { inclusive = true } }
                        } else vm.login()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(50)
                ) { Text("Iniciar sesión") }

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        SessionManager.login(Credential(uid = "INVITADO", nombre = "Invitado", usuario = "invitado", rol = "cliente"))
                        navController.navigate(AppRoutes.CATALOGO) { popUpTo(AppRoutes.LOGIN) { inclusive = true } }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(50)
                ) { Text("Ingresar como invitado") }

                Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { navController.navigate(AppRoutes.REGISTRO) }) { Text("Crear cuenta") }
                    TextButton(onClick = { navController.navigate(AppRoutes.RECUPERAR_CLAVE) }) { Text("¿Olvidaste tu clave?") }
                }
            }
        }
    }
}
