package com.example.cestaOganicaIA.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    titulo: String,
    subtitulo: String? = null,
    navController: NavController,
    carritoCount: Int = 0,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                if (subtitulo != null) {
                    Text(subtitulo, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                }
            }
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, "Menú")
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(AppRoutes.CARRITO) }) {
                BadgedBox(badge = { if (carritoCount > 0) Badge { Text("$carritoCount") } }) {
                    Icon(Icons.Default.ShoppingCart, "Carrito")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
