package com.example.cestaOganicaIA.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.session.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuertoScaffold(
    titulo: String,
    navController: NavController,
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isGuest = SessionManager.isGuest
    val isAdmin = SessionManager.isAdmin
    val current = SessionManager.currentUser

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (isGuest) "Menú Invitado" else "Hola, ${current?.nombre ?: "Usuario"}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                
                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.CATALOGO) },
                    icon = { Icon(Icons.Default.Storefront, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Mi perfil") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        if (isGuest) navController.navigate(AppRoutes.REGISTRO)
                        else navController.navigate(AppRoutes.PERFIL)
                    },
                    icon = { Icon(Icons.Default.Person, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Carrito de compras") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.CARRITO) },
                    icon = { Icon(Icons.Default.ShoppingCart, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Mis pedidos") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        if (isGuest) navController.navigate(AppRoutes.REGISTRO)
                        else navController.navigate(AppRoutes.HISTORIAL)
                    },
                    icon = { Icon(Icons.Default.History, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Favoritos") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        if (isGuest) navController.navigate(AppRoutes.REGISTRO)
                        else navController.navigate(AppRoutes.FAVORITOS)
                    },
                    icon = { Icon(Icons.Default.Favorite, null) }
                )

                NavigationDrawerItem(
                    label = { Text("Vista Informativa") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.BLOCK) },
                    icon = { Icon(Icons.Default.Info, null) }
                )

                if (isAdmin) {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("Administración", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 16.dp, bottom = 4.dp))
                    
                    NavigationDrawerItem(
                        label = { Text("Gestionar usuarios") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.ADMIN_USUARIOS) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, null) }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestionar stock") },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.ADMIN_STOCK) },
                        icon = { Icon(Icons.Default.Inventory, null) }
                    )
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        SessionManager.logout()
                        navController.navigate(AppRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                    },
                    icon = { Icon(Icons.Default.Logout, null) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    titulo = titulo,
                    navController = navController,
                    onNavigateBack = onBack,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            content = content
        )
    }
}
