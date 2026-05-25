package com.example.cestaOganicaIA.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    username: String,
    navController: NavController,
    viewModel: DrawerMenuViewModel
) {
    // Observamos las categorías del ViewModel
    val categoriasState by viewModel.categorias

    val current = SessionManager.currentUser

    // CORRECCIÓN: Usamos .uid en lugar de .idUsuario
    // También verificamos si el usuario actual coincide con las credenciales de Admin
    val isAdmin = current?.let { user ->
        user.uid == Credential.Admin.uid ||
                user.usuario.equals(Credential.Admin.usuario, ignoreCase = true)
    } ?: false

    val displayName = current?.nombre?.takeIf { it.isNotBlank() } ?: current?.usuario ?: username

    var menuOpen by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember {
        mutableStateOf<com.example.cestaOganicaIA.data.model.Categoria?>(null)
    }

    // Filtrado de productos basado en la categoría seleccionada
    val productosAMostrar = if (categoriaSeleccionada == null) {
        categoriasState.flatMap { it.productos }
    } else {
        categoriaSeleccionada!!.productos
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(displayName, style = MaterialTheme.typography.titleMedium)
                            current?.correo?.takeIf { it.isNotBlank() }?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Mi Perfil") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                onClick = { menuOpen = false; navController.navigate(AppRoutes.PERFIL) }
                            )
                            DropdownMenuItem(
                                text = { Text("Historial de pedidos") },
                                leadingIcon = { Icon(Icons.Default.History, null) },
                                onClick = { menuOpen = false; navController.navigate(AppRoutes.HISTORIAL) }
                            )
                            DropdownMenuItem(
                                text = { Text("Carrito") },
                                leadingIcon = { Icon(Icons.Default.ShoppingCart, null) },
                                onClick = { menuOpen = false; navController.navigate(AppRoutes.CARRITO) }
                            )

                            if (isAdmin) {
                                HorizontalDivider()
                                Text(
                                    "Panel de Control",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                DropdownMenuItem(
                                    text = { Text("Gestionar Usuarios") },
                                    leadingIcon = { Icon(Icons.Default.Group, null) },
                                    onClick = { menuOpen = false; navController.navigate(AppRoutes.ADMIN_USUARIOS) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Monitor de Pedidos") },
                                    leadingIcon = { Icon(Icons.Default.Assignment, null) },
                                    onClick = { menuOpen = false; navController.navigate(AppRoutes.ADMIN_PEDIDOS) }
                                )
                            }

                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.Default.Logout, null) },
                                onClick = {
                                    menuOpen = false
                                    SessionManager.logout()
                                    navController.navigate(AppRoutes.LOGIN) {
                                        popUpTo(0) { inclusive = true }
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
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // Selector de Categorías (LazyRow)
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = (categoriaSeleccionada == null),
                            onClick = { categoriaSeleccionada = null },
                            label = { Text("Todos") },
                            leadingIcon = { Icon(Icons.Default.Storefront, null) }
                        )
                    }
                    items(categoriasState) { categoria ->
                        FilterChip(
                            selected = (categoria.nombre == categoriaSeleccionada?.nombre),
                            onClick = { categoriaSeleccionada = categoria },
                            label = { Text(categoria.nombre) },
                            leadingIcon = { Icon(categoria.icono, null) }
                        )
                    }
                }

                // Lista de Productos (LazyColumn)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (productosAMostrar.isEmpty()) {
                        item {
                            Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No hay productos disponibles", color = Color.Gray)
                            }
                        }
                    } else {
                        items(productosAMostrar) { producto ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                onClick = {
                                    val n = Uri.encode(producto.nombre)
                                    val d = Uri.encode(producto.descripcion)
                                    navController.navigate("ProductoFormScreen/$n/${producto.precio}/$d/${producto.stock}/${producto.imagenResId}")
                                }
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = producto.imagenResId),
                                        contentDescription = producto.nombre,
                                        modifier = Modifier.size(100.dp).padding(8.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(producto.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(
                                                "Stock: ${producto.stock}",
                                                color = if (producto.stock < 5) Color.Red else Color.Gray,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                "$${producto.precio}",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}