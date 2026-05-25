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

    // Detectar éxito en el login desde el ViewModel y navegar
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            navController.navigate(AppRoutes.CATALOGO) {
                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    HuertoHogarTheme {
        Scaffold { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bienvenido a Cesta Orgánica",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Image(
                    painter = painterResource(R.drawable.logo_huerto_hogar),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.username,
                    onValueChange = vm::onUsernameChange,
                    label = { Text("Correo o Usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = vm::onPasswordChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (state.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // LÓGICA PARA ADMIN PREDEFINIDO
                        if (state.username == "admin" && state.password == "123") {
                            val adminUser = Credential(
                                uid = "ADMIN_STATIC",
                                usuario = "admin",
                                nombre = "Administrador Sistema",
                                correo = "admin@cesta.cl",
                                rol = "admin"
                            )
                            SessionManager.login(adminUser)
                            navController.navigate(AppRoutes.CATALOGO) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        } else {
                            vm.login()
                        }
                    },
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Iniciar sesión")
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        SessionManager.login(
                            Credential(
                                uid = "INVITADO",
                                nombre = "Invitado",
                                correo = "invitado@cesta.cl",
                                rol = "cliente"
                            )
                        )
                        navController.navigate(AppRoutes.CATALOGO) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Ingresar como invitado")
                }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { navController.navigate(AppRoutes.REGISTRO) }) {
                        Text("Crear cuenta", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = { navController.navigate(AppRoutes.RECUPERAR_CLAVE) }) {
                        Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}