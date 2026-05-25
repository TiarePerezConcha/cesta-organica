package com.example.cestaOganicaIA.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cestaOganicaIA.data.database.PedidoHistorial
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.session.SessionManager
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPedidosScreen(

    username: String,
    navController: NavController,
    viewModel: DrawerMenuViewModel
){
    val historial = PedidoHistorial.pedidos

    // Estado que viene del ViewModel
    val categoriasState = viewModel.categorias.value

    val huertoHogarColors = lightColorScheme(
        primary = Color(0xFF4CAF50),
        onPrimary = Color.White,
        secondary = Color(0xFFFF9800),
        onSecondary = Color.White,
        surface = Color(0xFFFFF8F5),
        onSurface = Color(0xFF3A3A3A)
    )
    val current = SessionManager.currentUser
    val displayName = current?.nombre?.takeIf { it.isNotBlank() } ?: current?.usuario ?: username
    val isAdmin =
        (current?.idUsuario == Credential.Admin.idUsuario) ||
                (current?.usuario?.equals(Credential.Admin.usuario, ignoreCase = true) == true)

    var menuOpen by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Perfil: $displayName",
                            style = MaterialTheme.typography.titleMedium
                        )
                        current?.correo?.takeIf { it.isNotBlank() }?.let { correo ->
                            Text(
                                text = correo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú de perfil")
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                navController.navigate("gestion_perfil")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Historial de pedidos") },
                            leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                navController.navigate("historial_pedidos")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Block") },
                            leadingIcon = { Icon(Icons.Default.Apps, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                navController.navigate("block")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Carrito (próximamente)") },
                            leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                navController.navigate("carrito")
                            }
                        )

                        if (isAdmin) {
                            DropdownMenuItem(
                                text = { Text("Gestionar usuarios") },
                                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                                onClick = {
                                    menuOpen = false
                                    navController.navigate("gestion_usuarios")
                                }
                            )
                        }

                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("Cerrar sesión") },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                SessionManager.currentUser = null // Assuming logout means clearing current user
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (historial.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aún no hay pedidos registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Como los id de Producto pueden repetirse (0 por defecto),
                // usamos un key estable alternativo:
                items(historial, key = { it.hashCode() }) { pedido ->
                    PedidoCard(pedido)
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("🎉 Pedido Realizado 🎉", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp)); HorizontalDivider(); Spacer(Modifier.height(8.dp))
            Text("• Producto: ${pedido.nombre}")
            Text("• Cantidad: ${pedido.cantidad}")
            Spacer(Modifier.height(8.dp)); HorizontalDivider(); Spacer(Modifier.height(8.dp))
            Text("Total pagado: ${pedido.precio}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}
