package com.example.cestaOganicaIA.ui.gestion

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.viewmodel.AdminViewModel
import com.example.cestaOganicaIA.ui.shared.AppRoutes
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
                        // BOTÓN PARA CARGAR PRODUCTOS PREDEFINIDOS A FIREBASE
                        IconButton(onClick = { 
                            viewModel.cargarCatalogoInicial()
                            Toast.makeText(context, "Subiendo productos a Firebase...", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Cargar Catálogo")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            if (ordenesAgrupadas.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No hay pedidos en la nube", color = Color.Gray)
                        Text("Usa el botón de arriba para subir el catálogo", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
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
        }
    }
}

@Composable
private fun AdminOrdenCard(
    ordenId: String,
    items: List<PedidoEntity>,
    onStatusChange: (String) -> Unit
) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerItem = items.first()
    val totalOrden = items.sumOf { it.total }
    
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ORDEN #$ordenId", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Text(primerItem.fechaPedido, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(Modifier.height(8.dp))
            
            val cliente = if (primerItem.usuarioId == "INVITADO") {
                "${primerItem.nombreContacto} (Invitado)"
            } else {
                primerItem.nombreContacto.takeIf { it.isNotBlank() } ?: "Usuario registrado"
            }
            
            Text("Cliente: $cliente", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            
            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            items.forEach { pedido ->
                Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (pedido.imagenResId != 0) {
                        Image(
                            painter = painterResource(pedido.imagenResId),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("${pedido.cantidad}x ${pedido.nombreProducto}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(formatoMoneda.format(pedido.total), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { showStatusDialog = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(primerItem.estado.uppercase(), style = MaterialTheme.typography.labelSmall)
                }
                
                Text(
                    text = "TOTAL: ${formatoMoneda.format(totalOrden)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showStatusDialog) {
        val estados = listOf("Confirmado", "En Camino", "Entregado", "Cancelado")
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Cambiar Estado") },
            text = {
                Column {
                    estados.forEach { estado ->
                        TextButton(
                            onClick = { 
                                onStatusChange(estado)
                                showStatusDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(estado) }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
