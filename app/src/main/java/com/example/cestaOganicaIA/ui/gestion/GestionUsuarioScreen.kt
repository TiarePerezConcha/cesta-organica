package com.example.cestaOganicaIA.ui.gestion

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuarioScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentAdmin = SessionManager.currentUser

    var usuariosList by remember { mutableStateOf<List<Credential>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    var editOpen by remember { mutableStateOf(false) }
    var editUser by remember { mutableStateOf<Credential?>(null) }

    // Cargar usuarios desde Firestore
    LaunchedEffect(Unit) {
        usuariosList = UserRepository.getAllUsers()
    }

    val usuariosFiltrados = usuariosList.filter {
        it.nombre.contains(query, ignoreCase = true) || 
        it.usuario.contains(query, ignoreCase = true) ||
        it.correo.contains(query, ignoreCase = true)
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Usuarios") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { inner ->
            Column(modifier = Modifier.padding(inner).fillMaxSize().padding(16.dp)) {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    placeholder = { Text("Buscar usuario...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                if (usuariosFiltrados.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay usuarios registrados", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(usuariosFiltrados, key = { it.uid }) { u ->
                            UserCard(
                                user = u,
                                isMe = u.uid == currentAdmin?.uid,
                                onEdit = { editUser = u; editOpen = true }
                            )
                        }
                    }
                }
            }

            if (editOpen && editUser != null) {
                EditUserDialog(
                    user = editUser!!,
                    onDismiss = { editOpen = false },
                    onSave = { updated ->
                        scope.launch {
                            UserRepository.updateProfile(updated.uid, updated.nombre, updated.telefono, updated.direccion)
                                .onSuccess {
                                    usuariosList = UserRepository.getAllUsers()
                                    editOpen = false
                                    Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun UserCard(user: Credential, isMe: Boolean, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (isMe) "${user.nombre} (Tú)" else user.nombre, fontWeight = FontWeight.Bold)
                Text(user.correo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
private fun EditUserDialog(user: Credential, onDismiss: () -> Unit, onSave: (Credential) -> Unit) {
    var nombre by remember { mutableStateOf(user.nombre) }
    var telefono by remember { mutableStateOf(user.telefono) }
    var direccion by remember { mutableStateOf(user.direccion) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
            }
        },
        confirmButton = {
            Button(onClick = { onSave(user.copy(nombre = nombre, telefono = telefono, direccion = direccion)) }) {
                Text("Guardar")
            }
        }
    )
}
