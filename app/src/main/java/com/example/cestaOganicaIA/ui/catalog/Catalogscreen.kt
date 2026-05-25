package com.example.cestaOganicaIA.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Storefront
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
import com.example.cestaOganicaIA.data.model.CatalogoItem
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.AppTopBar
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.CatalogViewModel

@Composable
fun CatalogScreen(navController: NavController, vm: CatalogViewModel) {
    val categorias by vm.categorias.collectAsState()
    val favoritosNombres by vm.favoritosNombres.collectAsState()
    val carritoCount by vm.carritoCount.collectAsState()

    val uid = SessionManager.currentUser?.uid ?: ""
    val displayName = SessionManager.currentUser?.nombre?.takeIf { it.isNotBlank() }
        ?: SessionManager.currentUser?.usuario ?: "Usuario"

    var categoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    val productosAMostrar = if (categoriaSeleccionada == null)
        categorias.flatMap { it.productos }
    else
        categorias.find { it.nombre == categoriaSeleccionada }?.productos ?: emptyList()

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                AppTopBar(
                    titulo = "Hola, $displayName",
                    subtitulo = SessionManager.currentUser?.correo,
                    navController = navController,
                    carritoCount = carritoCount
                )
            }
        ) { inner ->
            Column(modifier = Modifier.padding(inner).fillMaxSize()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    item {
                        FilterChip(
                            selected = categoriaSeleccionada == null,
                            onClick = { categoriaSeleccionada = null },
                            label = { Text("Todos") },
                            leadingIcon = { Icon(Icons.Default.Storefront, null) }
                        )
                    }
                    items(categorias) { cat ->
                        FilterChip(
                            selected = cat.nombre == categoriaSeleccionada,
                            onClick = { categoriaSeleccionada = cat.nombre },
                            label = { Text(cat.nombre) },
                            leadingIcon = { Icon(cat.icono, null) }
                        )
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(productosAMostrar) { producto ->
                        ProductoCard(
                            producto = producto,
                            esFavorito = producto.nombre in favoritosNombres,
                            onVerDetalle = {
                                navController.navigate("ProductoFormScreen/${producto.nombre}/${producto.precio}/${producto.descripcion}/${producto.stock}/${producto.imagenResId}")
                            },
                            onToggleFavorito = {
                                if (uid.isNotEmpty()) vm.toggleFavorito(uid, producto.nombre)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(
    producto: CatalogoItem,
    esFavorito: Boolean,
    onVerDetalle: () -> Unit,
    onToggleFavorito: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onVerDetalle
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(producto.imagenResId),
                contentDescription = null,
                modifier = Modifier.size(90.dp).padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(vertical = 10.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("\$${producto.precio}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onToggleFavorito) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (esFavorito) Color.Red else Color.Gray
                )
            }
        }
    }
}
