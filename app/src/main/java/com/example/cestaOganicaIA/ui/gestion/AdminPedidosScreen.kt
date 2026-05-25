package com.example.cestaOganicaIA.ui.gestion

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
import androidx.compose.ui.res.painterResource
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
    val todosPedidos by viewModel.todosPedidos.collectAsState()
    
    val ordenesAgrupadas = remember(todosPedidos) {
        todosPedidos.groupBy { it.ordenId }
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Pedidos") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            if (ordenesAgrupadas.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("No hay pedidos registrados", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ordenesAgrupadas.entries.toList()) { entry ->
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
    ordenId: Int,
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("PEDIDO #$ordenId", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Cliente: ${primerItem.nombreContacto}", style = MaterialTheme.typography.bodyMedium)
            Text("Estado: ${primerItem.estado}", fontWeight = FontWeight.SemiBold)
            
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            
            items.forEach { item ->
                Text("${item.cantidad}x ${item.nombreProducto} - ${formatoMoneda.format(item.total)}", fontSize = 14.sp)
            }
            
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total: ${formatoMoneda.format(totalOrden)}", fontWeight = FontWeight.ExtraBold)
                Button(onClick = { showStatusDialog = true }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("Cambiar Estado", fontSize = 12.sp)
                }
            }
        }
    }

    if (showStatusDialog) {
        val estados = listOf("Confirmado", "Preparando", "En Camino", "Entregado", "Cancelado")
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Actualizar Estado") },
            text = {
                Column {
                    estados.forEach { estado ->
                        Text(estado, modifier = Modifier.fillMaxWidth().clickable { 
                            onStatusChange(estado)
                            showStatusDialog = false
                        }.padding(16.dp))
                    }
                }
            },
            confirmButton = {}
        )
    }
}
