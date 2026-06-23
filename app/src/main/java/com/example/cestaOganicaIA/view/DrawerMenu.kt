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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel

@Composable
fun DrawerMenu(
    username: String,
    navController: NavController,
    viewModel: DrawerMenuViewModel
) {
    // 1. Obtenemos el flujo del usuario actual para monitorear la sesión
    val currentBySession by SessionManager.currentUserFlow.collectAsState()

    // 2. CORRECCIÓN: Si el usuario pasa a ser null (Logout), redirigimos inmediatamente al Login
    LaunchedEffect(currentBySession) {
        if (currentBySession == null) {
            navController.navigate(AppRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val categoriasState = viewModel.categorias.value
    var categoriaSeleccionada by remember {
        mutableStateOf<com.example.cestaOganicaIA.data.model.Categoria?>(null)
    }

    LaunchedEffect(categoriasState) {
        if (categoriaSeleccionada != null && !categoriasState.contains(categoriaSeleccionada)) {
            categoriaSeleccionada = null
        }
    }

    val productosAMostrar = if (categoriaSeleccionada == null) {
        categoriasState.flatMap { it.productos }
    } else {
        categoriaSeleccionada!!.productos
    }

    HuertoScaffold(
        titulo = "Catálogo",
        navController = navController
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
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

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(productosAMostrar) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        onClick = {
                            val nombreNav = Uri.encode(producto.nombre)
                            val descripcionNav = Uri.encode(producto.descripcion)
                            navController.navigate("ProductoFormScreen/$nombreNav/${producto.precio}/$descripcionNav/${producto.stock}")
                        }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = producto.imagenResId),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp).padding(8.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = producto.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Stock: ${producto.stock}", style = MaterialTheme.typography.bodySmall, color = if (producto.stock < 10) Color.Red else Color.Gray)
                                Text(
                                    text = "$${producto.precio}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = "@ 2026 CestaOrganica App",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}