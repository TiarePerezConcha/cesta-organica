package com.example.cestaOganicaIA.ui.gestion

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.viewmodel.AdminViewModel
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val todosPedidos by viewModel.todosPedidos.collectAsState()
    var showAddProductDialog by remember { mutableStateOf(false) }
    
    val ordenesAgrupadas = remember(todosPedidos) {
        todosPedidos.groupBy { it.ordenId }
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Monitor Global") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            viewModel.cargarCatalogoInicial()
                            Toast.makeText(context, "Sincronizando...", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.CloudSync, contentDescription = "Sincronizar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, "Nuevo Producto", tint = Color.White)
                }
            }
        ) { padding ->
            if (ordenesAgrupadas.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Sin pedidos registrados", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ordenesAgrupadas.entries.toList(), key = { it.key }) { entry ->
                        AdminOrdenCard(
                            ordenId = entry.key,
                            items = entry.value,
                            onStatusChange = { nuevoEstado ->
                                viewModel.actualizarEstadoOrden(entry.key, nuevoEstado)
                            }
                        )
                    }
                }
            }

            if (showAddProductDialog) {
                AddProductDialog(
                    onDismiss = { showAddProductDialog = false },
                    onSave = { nuevoProd ->
                        viewModel.agregarProductoManual(nuevoProd)
                        showAddProductDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onSave: (com.example.cestaOganicaIA.data.model.Producto) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Frutas") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") })
                
                val categorias = listOf("Frutas", "Verduras", "Orgánicos", "Lácteos")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    categorias.forEach { c ->
                        FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c, fontSize = 10.sp) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                if(nombre.isNotBlank()) onSave(com.example.cestaOganicaIA.data.model.Producto(nombre = nombre, precio = precio, stock = stock.toIntOrNull() ?: 0, descripcion = desc, categoria = cat, imagenResId = com.example.cestaOganicaIA.R.drawable.manzana_fuji)) 
            }) { Text("Guardar") }
        }
    )
}

@Composable
private fun AdminOrdenCard(ordenId: String, items: List<PedidoEntity>, onStatusChange: (String) -> Unit) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerItem = items.first()
    val totalOrden = items.sumOf { it.total }
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ORDEN #$ordenId", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(primerItem.fechaPedido, style = MaterialTheme.typography.bodySmall)
            }
            val cliente = if (primerItem.usuarioId == "INVITADO") "${primerItem.nombreContacto} (Invitado)" else primerItem.nombreContacto
            Text("Cliente: $cliente", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            items.forEach { Text("• ${it.cantidad}x ${it.nombreProducto}", style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { showStatusDialog = true }) { Text(primerItem.estado) }
                Text("Total: ${formatoMoneda.format(totalOrden)}", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Estado") },
            text = {
                Column {
                    listOf("Confirmado", "En Camino", "Entregado").forEach { 
                        TextButton(onClick = { onStatusChange(it); showStatusDialog = false }) { Text(it) }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
