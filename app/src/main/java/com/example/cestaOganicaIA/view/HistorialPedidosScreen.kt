package com.example.cestaOganicaIA.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.viewmodel.HistorialViewModel
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPedidosScreen(
    navController: NavController,
    viewModel: HistorialViewModel
) {
    val user = SessionManager.currentUser
    val pedidos by viewModel.pedidos.collectAsState()
    val displayName = user?.nombre?.takeIf { it.isNotBlank() } ?: user?.usuario ?: "Usuario"

    LaunchedEffect(user) {
        user?.let { viewModel.cargarPedidos(it.uid) } // Corregido: usa uid (String)
    }

    // Agrupamos los pedidos por ordenId para que compras de un mismo carrito salgan juntas
    val pedidosAgrupados = remember(pedidos) {
        pedidos.groupBy { it.ordenId }
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = "Mis Compras", style = MaterialTheme.typography.titleMedium)
                            Text(text = displayName, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(AppRoutes.CATALOGO) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            if (pedidos.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("No tienes pedidos registrados", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pedidosAgrupados.entries.toList(), key = { it.key }) { entry ->
                        OrdenCard(ordenId = entry.key, items = entry.value)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdenCard(ordenId: String, items: List<PedidoEntity>) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    val primerItem = items.first()
    val totalOrden = items.sumOf { it.total }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Cabecera de la Orden
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ORDEN #$ordenId", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Text(primerItem.fechaPedido, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Mostrar Nombre del Cliente si existe (especialmente para invitados)
            val clienteNombre = primerItem.nombreContacto.takeIf { it.isNotBlank() } ?: "Cliente Registrado"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Recibe: $clienteNombre", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))

            // Lista de productos dentro de esta orden
            items.forEach { pedido ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pedido.imagenResId != 0) {
                        Image(
                            painter = painterResource(pedido.imagenResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.White, RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(pedido.nombreProducto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Cant: ${pedido.cantidad} • ${formatoMoneda.format(pedido.precioUnitario)} c/u", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(formatoMoneda.format(pedido.total), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            // Información de entrega (compartida por la orden)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Entrega programada: ${primerItem.fechaEntrega}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(8.dp))
                    Text(primerItem.direccionEntrega, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                // Correo y Teléfono si es Invitado
                if (primerItem.usuarioId == "INVITADO") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContactPhone, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text("${primerItem.correoContacto} | ${primerItem.telefonoContacto}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = primerItem.estado.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
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
}
