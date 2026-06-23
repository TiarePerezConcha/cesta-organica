package com.example.cestaOganicaIA.ui.shared

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val current by SessionManager.currentUserFlow.collectAsState()
    val isGuest = current == null || current?.uid == "INVITADO"
    val isAdmin = current?.rol == "admin"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    // Evitamos que falle si 'current' es null usando un texto genérico de respaldo
                    text = if (isGuest) "Menú Invitado" else "Hola, ${current?.nombre ?: "Usuario"}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() }; navController.navigate(AppRoutes.CATALOGO) },
                    icon = { Icon(Icons.Default.Store, null) }
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
                    icon = { Icon(Icons.Default.ChangeHistory, null) }
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
                    Text("Administration", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(start = 16.dp, bottom = 4.dp))

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
                        scope.launch {
                            // 1. Cerramos el panel lateral de forma asíncrona
                            drawerState.close()

                            // 2. Borramos los datos en el SessionManager
                            SessionManager.logout()

                            // 3. REINICIO TOTAL IMPERATIVO:
                            // Pasamos por alto el bug del backstack recreando el Intent de la app
                            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                context.startActivity(intent)

                                // Finalizamos la instancia de la actividad actual
                                (context as? Activity)?.finish()
                            }
                        }
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