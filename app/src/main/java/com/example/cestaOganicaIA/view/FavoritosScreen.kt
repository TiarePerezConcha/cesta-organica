package com.example.cestaOganicaIA.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HeartBroken
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
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.CatalogViewModel

@Composable
fun FavoritosScreen(navController: NavController, vm: CatalogViewModel) {
    val categorias by vm.categorias.collectAsState()
    val favoritosNombres by vm.favoritosNombres.collectAsState()
    val uid = SessionManager.currentUser?.uid ?: ""

    // Filtrar todos los productos que están en la lista de favoritos
    val favoritosAMostrar = remember(categorias, favoritosNombres) {
        categorias.flatMap { it.productos }.filter { it.nombre in favoritosNombres }
    }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) vm.cargarDatosUsuario(uid)
    }

    HuertoHogarTheme {
        HuertoScaffold(
            titulo = "Mis Favoritos",
            navController = navController,
            onBack = { navController.popBackStack() }
        ) { inner ->
            if (favoritosAMostrar.isEmpty()) {
                Box(Modifier.padding(inner).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("No tienes productos favoritos.", color = Color.Gray)
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Volver al catálogo")
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(inner).fillMaxSize()) {
                    items(favoritosAMostrar) { producto ->
                        FavoritoCard(
                            producto = producto,
                            onVerDetalle = {
                                val nombreNav = Uri.encode(producto.nombre)
                                val descripcionNav = Uri.encode(producto.descripcion)
                                navController.navigate("ProductoFormScreen/$nombreNav/${producto.precio}/$descripcionNav/${producto.stock}/${producto.imagenResId}")
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
private fun FavoritoCard(
    producto: CatalogoItem,
    onVerDetalle: () -> Unit,
    onToggleFavorito: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onVerDetalle
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val imgRes = if (producto.imagenResId != 0) producto.imagenResId else com.example.cestaOganicaIA.R.drawable.logoduoc
            Image(
                painter = painterResource(imgRes),
                contentDescription = null,
                modifier = Modifier.size(90.dp).padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(vertical = 10.dp)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${producto.precio}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onToggleFavorito) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}
