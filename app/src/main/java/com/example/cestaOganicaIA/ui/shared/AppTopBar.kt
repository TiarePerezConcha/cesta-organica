package com.example.cestaOganicaIA.ui.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.session.SessionManager

/**
 * TopAppBar reutilizable con menú de perfil.
 * Se usa en todas las pantallas principales para evitar duplicación de código.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    subtitulo: String? = null,
    navController: NavController,
    carritoCount: Int = 0,
    onNavigateBack: (() -> Unit)? = null
) {
    val current = SessionManager.currentUser
    var menuOpen by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            if (subtitulo != null) {
                androidx.compose.foundation.layout.Column {
                    Text(titulo, style = MaterialTheme.typography.titleMedium)
                    Text(subtitulo, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                }
            } else {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
            }
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        actions = {
            // Ícono del carrito con badge
            if (!SessionManager.isGuest) {
                BadgedBox(badge = {
                    if (carritoCount > 0) Badge { Text("$carritoCount") }
                }) {
                    IconButton(onClick = { navController.navigate(AppRoutes.CARRITO) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            // Menú de perfil
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.onPrimary)
            }

            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {

                DropdownMenuItem(
                    text = { Text("Mi perfil") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    onClick = { menuOpen = false; navController.navigate(AppRoutes.PERFIL) }
                )

                if (!SessionManager.isGuest) {
                    DropdownMenuItem(
                        text = { Text("Mis pedidos") },
                        leadingIcon = { Icon(Icons.Default.History, null) },
                        onClick = { menuOpen = false; navController.navigate(AppRoutes.HISTORIAL) }
                    )
                    DropdownMenuItem(
                        text = { Text("Mis favoritos") },
                        leadingIcon = { Icon(Icons.Default.Favorite, null) },
                        onClick = { menuOpen = false; navController.navigate(AppRoutes.FAVORITOS) }
                    )
                }

                DropdownMenuItem(
                    text = { Text("Información") },
                    leadingIcon = { Icon(Icons.Default.Info, null) },
                    onClick = { menuOpen = false; navController.navigate(AppRoutes.INFO) }
                )

                if (SessionManager.isAdmin) {
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Gestionar usuarios") },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null) },
                        onClick = { menuOpen = false; navController.navigate(AppRoutes.ADMIN_USUARIOS) }
                    )
                    DropdownMenuItem(
                        text = { Text("Gestionar stock") },
                        leadingIcon = { Icon(Icons.Default.Inventory, null) },
                        onClick = { menuOpen = false; navController.navigate(AppRoutes.ADMIN_STOCK) }
                    )
                }

                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                    leadingIcon = { Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error) },
                    onClick = {
                        menuOpen = false
                        SessionManager.logout()
                        navController.navigate(AppRoutes.LOGIN) {
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