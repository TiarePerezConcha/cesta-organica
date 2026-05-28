package com.example.cestaOganicaIA.view

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
import java.util.Locale

@Composable
fun HistorialPedidosScreen(
    navController: NavController,
    viewModel: HistorialViewModel
) {
    val user = SessionManager.currentUser
    val uid = user?.uid ?: "INVITADO"
    val pedidos by viewModel.pedidos.collectAsState()

    LaunchedEffect(uid) {
        viewModel.cargarPedidos(uid)
    }

    // Agrupar pedidos por ordenId
    val pedidosAgrupados = remember(pedidos) {
        pedidos.groupBy { it.ordenId }
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
                    items(pedidosAgrupados.keys.toList()) { ordenId ->
                        val itemsDeOrden = pedidosAgrupados[ordenId] ?: emptyList()
                        OrdenCard(ordenId, itemsDeOrden)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdenCard(ordenId: String, items: List<PedidoEntity>) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerPedido = items.first()
    val totalOrden = items.sumOf { it.total }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
        }
    }
}
