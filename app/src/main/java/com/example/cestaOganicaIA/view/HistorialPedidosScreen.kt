package com.example.cestaOganicaIA.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.HistorialViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistorialPedidosScreen(
    navController: NavController,
    viewModel: HistorialViewModel
) {
    val user = SessionManager.currentUser
    val uid = user?.uid ?: "INVITADO"
    val pedidos by viewModel.pedidos.collectAsState()

    var ordenSeleccionada by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        viewModel.cargarPedidos(uid)
    }

    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    fun parsearFecha(fecha: String): Long = try {
        formatoFecha.parse(fecha)?.time ?: 0L
    } catch (e: Exception) { 0L }

    // Agrupar pedidos por ordenId, preservando el orden por fecha real (más reciente primero)
    val ordenesOrdenadas = remember(pedidos) {
        pedidos.groupBy { it.ordenId }
            .toList()
            .sortedByDescending { (_, items) -> parsearFecha(items.first().fechaPedido) }
    }

    val ordenParaDetalle = ordenSeleccionada?.let { id ->
        ordenesOrdenadas.find { it.first == id }
    }

    HuertoHogarTheme {
        HuertoScaffold(
            titulo = "Mis Pedidos",
            onBack = { navController.popBackStack() },
            navController = navController
        ) { padding ->
            if (pedidos.isEmpty()) {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("Aún no tienes pedidos registrados.", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ordenesOrdenadas) { (ordenId, itemsDeOrden) ->
                        OrdenCard(
                            ordenId = ordenId,
                            items = itemsDeOrden,
                            onClick = { ordenSeleccionada = ordenId }
                        )
                    }
                }
            }
        }
    }

    if (ordenParaDetalle != null) {
        DetallePedidoDialog(
            ordenId = ordenParaDetalle.first,
            items = ordenParaDetalle.second,
            onDismiss = { ordenSeleccionada = null }
        )
    }
}

@Composable
private fun OrdenCard(ordenId: String, items: List<PedidoEntity>, onClick: () -> Unit) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerPedido = items.first()
    val totalOrden = items.sumOf { it.total }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Orden #$ordenId", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(primerPedido.fechaPedido, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))

            Text("Productos:", fontWeight = FontWeight.SemiBold)
            items.forEach { item ->
                Text("• ${item.nombreProducto} (x${item.cantidad}) - ${formatoMoneda.format(item.total)}")
            }

            Spacer(Modifier.height(8.dp))

            Text("Estado: ${primerPedido.estado}", color = when(primerPedido.estado) {
                "Confirmado" -> Color.Blue
                "En Camino" -> Color(0xFFFFA500)
                "Entregado" -> Color(0xFF4CAF50)
                else -> Color.Gray
            }, fontWeight = FontWeight.Bold)

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total de la compra:", fontWeight = FontWeight.Bold)
                Text(formatoMoneda.format(totalOrden), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Toca para ver el detalle",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun DetallePedidoDialog(ordenId: String, items: List<PedidoEntity>, onDismiss: () -> Unit) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerPedido = items.first()
    val totalOrden = items.sumOf { it.total }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalle del Pedido #$ordenId") },
        text = {
            Column {
                DetalleSeccion(titulo = "Fecha del pedido", valor = primerPedido.fechaPedido)
                DetalleSeccion(titulo = "Fecha de entrega", valor = primerPedido.fechaEntrega)
                DetalleSeccion(titulo = "Estado", valor = primerPedido.estado)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Text("Datos de Contacto", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                DetalleSeccion(titulo = "Nombre", valor = primerPedido.nombreContacto)
                DetalleSeccion(titulo = "Correo", valor = primerPedido.correoContacto)
                DetalleSeccion(titulo = "Teléfono", valor = primerPedido.telefonoContacto)
                DetalleSeccion(titulo = "Dirección de entrega", valor = primerPedido.direccionEntrega)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Text("Productos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                items.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.nombreProducto} (x${item.cantidad})")
                        Text(formatoMoneda.format(item.total))
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total:", fontWeight = FontWeight.Bold)
                    Text(
                        formatoMoneda.format(totalOrden),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
private fun DetalleSeccion(titulo: String, valor: String) {
    Column(Modifier.padding(vertical = 2.dp)) {
        Text(titulo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(
            if (valor.isBlank()) "No especificado" else valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}