package com.example.cestaOganicaIA.ui.gestion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionPerfilScreen(navController: NavController) {
    // Obtenemos el usuario actual de la sesión
    val current = SessionManager.currentUser
    val scope = rememberCoroutineScope()

    // Si no hay sesión, redirigir al login inmediatamente
    if (current == null) {
        LaunchedEffect(Unit) {
            navController.navigate(AppRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    // Configuración de colores personalizada para la marca "Cesta Orgánica"
    val colors = lightColorScheme(
        primary    = Color(0xFF4CAF50),
        onPrimary  = Color.White,
        secondary  = Color(0xFFFF9800),
        onSecondary= Color.White,
        surface    = Color(0xFFFFF8F5),
        onSurface  = Color(0xFF3A3A3A),
        error      = Color(0xFFB00020)
    )

    MaterialTheme(colorScheme = colors) {
        // Estados para los campos editables
        var nombre by remember { mutableStateOf(current.nombre) }
        var telefono by remember { mutableStateOf(current.telefono) }
        var direccion by remember { mutableStateOf(current.direccion) }

        // Estados para manejo de errores y feedback
        var generalErr by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var showOk by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Mis Datos del Perfil", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colors.primary,
                        titleContentColor = colors.onPrimary
                    )
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .background(colors.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Información de solo lectura ---
                OutlinedTextField(
                    value = current.usuario,
                    onValueChange = {},
                    label = { Text("Nombre de Usuario") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.LightGray)
                )

                OutlinedTextField(
                    value = current.correo,
                    onValueChange = {},
                    label = { Text("Correo Electrónico") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.LightGray)
                )

                // --- Campos Editables ---
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { t ->
                        // Solo números y máximo 9 dígitos según requerimiento
                        if (t.all { it.isDigit() } && t.length <= 9) telefono = t
                    },
                    label = { Text("Teléfono de Contacto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de Entrega") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de error dinámico
                if (generalErr != null) {
                    Text(generalErr!!, color = colors.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(8.dp))

                // Botón Guardar con lógica de actualización corregida
                Button(
                    onClick = {
                        val n = nombre.trim()
                        val t = telefono.trim()
                        val d = direccion.trim()

                        if (n.isEmpty() || t.length < 8 || d.isEmpty()) {
                            generalErr = "Por favor, completa todos los campos correctamente."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            generalErr = null

                            // Llamamos al repositorio que ahora devuelve Result<Credential>
                            val result = UserRepository.updateProfile(
                                uid = current.uid, // Usamos uid directamente del modelo
                                nombre = n,
                                telefono = t,
                                direccion = d
                            )

                            result.onSuccess { usuarioActualizado ->
                                // Ahora pasamos el objeto Credential real, no un Unit
                                SessionManager.updateCurrent(usuarioActualizado)
                                showOk = true
                                isLoading = false
                            }.onFailure { e ->
                                generalErr = "Error al actualizar: ${e.localizedMessage}"
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Guardar Cambios")
                }

                // Botón Volver
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary)
                ) {
                    Text("Volver al Catálogo")
                }
            }

            // Diálogo de confirmación de éxito
            if (showOk) {
                AlertDialog(
                    onDismissRequest = { showOk = false },
                    title = { Text("Perfil Actualizado") },
                    text = { Text("Tus datos se han guardado correctamente en el sistema.") },
                    confirmButton = {
                        TextButton(onClick = { showOk = false }) { Text("Aceptar") }
                    }
                )
            }
        }
    }
}