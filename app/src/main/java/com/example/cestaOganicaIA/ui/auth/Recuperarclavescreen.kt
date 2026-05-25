package com.example.cestaOganicaIA.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RecuperarClaveScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showOk by remember { mutableStateOf(false) }

    HuertoHogarTheme {
        Scaffold { inner ->
            Column(
                modifier = Modifier.padding(inner).fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Recuperar contraseña", style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp))

                Text("Ingresa tu correo y te enviaremos un enlace para restablecer tu contraseña.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Ingresa tu correo", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email.trim())
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    showOk = true
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Enviar enlace")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Volver") }
            }

            if (showOk) {
                AlertDialog(
                    onDismissRequest = { showOk = false },
                    title = { Text("Correo enviado") },
                    text = { Text("Se ha enviado un enlace a $email. Revisa tu bandeja de entrada.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showOk = false
                            navController.navigate(AppRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) { Text("Volver al Login") }
                    }
                )
            }
        }
    }
}
