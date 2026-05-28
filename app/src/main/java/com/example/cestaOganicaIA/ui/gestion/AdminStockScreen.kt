package com.example.cestaOganicaIA.ui.gestion

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cestaOganicaIA.R
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.GestionStockViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockScreen(navController: NavController, viewModel: GestionStockViewModel) {
    val productos by viewModel.productos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }

    val categoriasExistentes = productos.map { it.categoria }.distinct().filter { it.isNotBlank() }

    HuertoHogarTheme {
        HuertoScaffold(
            titulo = "Gestión de Stock",
            navController = navController
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.preCargarProductos() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Precargar")
                    }
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Nuevo")
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (productos.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hay productos en stock. Usa 'Precargar' o 'Nuevo'.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(productos) { producto ->
                            StockItemCard(
                                producto = producto,
                                onEdit = { productoAEditar = producto },
                                onDelete = { viewModel.eliminarProducto(producto) }
                            )
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.limpiarTodo() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar Todo el Stock")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProductoDialog(
            categoriasExistentes = categoriasExistentes,
            onDismiss = { showAddDialog = false },
            onConfirm = { n, p, s, d, c, imgRes, imgUri ->
                viewModel.agregarProducto(n, p, s, d, c, imgRes, imgUri)
                showAddDialog = false
            }
        )
    }

    if (productoAEditar != null) {
        ProductoDialog(
            producto = productoAEditar,
            categoriasExistentes = categoriasExistentes,
            onDismiss = { productoAEditar = null },
            onConfirm = { n, p, s, d, c, imgRes, imgUri ->
                viewModel.actualizarProducto(productoAEditar!!.copy(
                    nombre = n, precio = p, stock = s, descripcion = d, categoria = c, imagenResId = imgRes, imagenUri = imgUri
                ))
                productoAEditar = null
            }
        )
    }
}

@Composable
fun StockItemCard(producto: Producto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (producto.imagenUri != null) {
                AsyncImage(
                    model = producto.imagenUri,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                val imgRes = if (producto.imagenResId != 0) producto.imagenResId else R.drawable.logoduoc
                Image(
                    painter = painterResource(imgRes),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Precio: $${producto.precio} | Stock: ${producto.stock}", style = MaterialTheme.typography.bodyMedium)
                Text("Cat: ${producto.categoria}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Color.Blue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDialog(
    producto: Producto? = null,
    categoriasExistentes: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, String, Int, String?) -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precio by remember { mutableStateOf(producto?.precio ?: "") }
    var stock by remember { mutableStateOf(producto?.stock?.toString() ?: "0") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    
    var imagenResSeleccionada by remember { mutableStateOf(producto?.imagenResId ?: R.drawable.logoduoc) }
    var imagenUriSeleccionada by remember { mutableStateOf<String?>(producto?.imagenUri) }
    
    var categoriaSeleccionada by remember { 
        mutableStateOf(producto?.categoria ?: if (categoriasExistentes.isNotEmpty()) categoriasExistentes[0] else "General") 
    }
    var nuevaCategoria by remember { mutableStateOf("") }
    var modoNuevaCategoria by remember { mutableStateOf(categoriasExistentes.isEmpty()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedUri = saveImageToInternalStorage(context, it)
            imagenUriSeleccionada = savedUri
            imagenResSeleccionada = 0
        }
    }

    val imagenesDisponibles = listOf(
        R.drawable.manzana_fuji, R.drawable.platano_cavendish, R.drawable.zanahorias,
        R.drawable.espinaca, R.drawable.miel_organica, R.drawable.pimientos,
        R.drawable.naranja_valencia, R.drawable.quinua_organica, R.drawable.leche_entera,
        R.drawable.logoduoc
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (producto == null) "Agregar Producto" else "Editar Producto") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                }
                
                item {
                    Text("Imagen del Producto:", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("Cargar Galería")
                        }
                        Spacer(Modifier.width(8.dp))
                        if (imagenUriSeleccionada != null) {
                            AsyncImage(
                                model = imagenUriSeleccionada,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("O elige una prediseñada:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(imagenesDisponibles) { img ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable { 
                                        imagenResSeleccionada = img
                                        imagenUriSeleccionada = null
                                    }
                                    .padding(2.dp)
                            ) {
                                Image(
                                    painter = painterResource(img),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    alpha = if (imagenResSeleccionada == img && imagenUriSeleccionada == null) 1f else 0.5f
                                )
                                if (imagenResSeleccionada == img && imagenUriSeleccionada == null) {
                                    Icon(Icons.Default.Check, null, tint = Color.Green, modifier = Modifier.align(Alignment.TopEnd))
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Categoría:", style = MaterialTheme.typography.labelMedium)
                    if (!modoNuevaCategoria) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = categoriaSeleccionada,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                categoriasExistentes.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            categoriaSeleccionada = cat
                                            expanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("+ Nueva Categoría", color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        modoNuevaCategoria = true
                                        expanded = false
                                    }
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = nuevaCategoria,
                            onValueChange = { nuevaCategoria = it },
                            label = { Text("Nombre de nueva categoría") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                if (categoriasExistentes.isNotEmpty()) {
                                    IconButton(onClick = { modoNuevaCategoria = false }) {
                                        Icon(Icons.Default.Close, "Ver existentes")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val finalCat = if (modoNuevaCategoria) nuevaCategoria.trim() else categoriaSeleccionada
                if (nombre.isNotBlank() && precio.isNotBlank() && finalCat.isNotBlank()) {
                    onConfirm(nombre, precio, stock.toIntOrNull() ?: 0, descripcion, finalCat, imagenResSeleccionada, imagenUriSeleccionada)
                }
            }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "prod_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
