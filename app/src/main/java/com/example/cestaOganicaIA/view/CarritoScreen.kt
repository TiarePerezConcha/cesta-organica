package com.example.cestaOganicaIA.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.CarritoViewModel
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import java.text.NumberFormat
import java.util.*
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    drawerMenuViewModel: DrawerMenuViewModel
) {
    val context = LocalContext.current
    val user = SessionManager.currentUser
    val items by carritoViewModel.items.collectAsState()
    val total by carritoViewModel.total.collectAsState()
    val cantidadTotal by carritoViewModel.cantidadTotal.collectAsState()
    val pedidoConfirmado by carritoViewModel.pedidoConfirmado.collectAsState()

    val currentUid = user?.uid ?: "INVITADO"
    val esInvitado = currentUid == "INVITADO"

    var menuOpen by remember { mutableStateOf(false) }
    var usarDireccionRegistrada by remember { mutableStateOf(!esInvitado) }
    var direccionPersonalizada by remember { mutableStateOf("") }
    var fechaEntrega by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var nombreInvitado by remember { mutableStateOf("") }
    var correoInvitado by remember { mutableStateOf("") }
    var telefonoInvitado by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                return utcTimeMillis >= calendar.timeInMillis
            }
        }
    )
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    LaunchedEffect(currentUid) {
        if (currentUid.isNotEmpty()) {
            carritoViewModel.cargarCarrito(currentUid)
        }
    }

    if (pedidoConfirmado) {
        AlertDialog(
            onDismissRequest = { carritoViewModel.resetPedidoConfirmado() },
            title = { Text("¡Compra Exitosa!") },
            text = { Text("Tu pedido ha sido procesado correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    carritoViewModel.resetPedidoConfirmado()
                    navController.navigate(AppRoutes.HISTORIAL) {
                        popUpTo(AppRoutes.CATALOGO)
                    }
                }) { Text("Ir al Historial") }
            }
        )
    }

    HuertoHogarTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mi Carrito") },
                    actions = {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Catálogo") },
                                leadingIcon = { Icon(Icons.Default.Storefront, null) },
                                onClick = { menuOpen = false; navController.navigate(AppRoutes.CATALOGO) }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión") },
                                leadingIcon = { Icon(Icons.Default.Logout, null) },
                                onClick = {
                                    menuOpen = false
                                    SessionManager.logout()
                                    navController.navigate(AppRoutes.LOGIN) { popUpTo(0) }
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                if (items.isNotEmpty()) {
                    Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total ($cantidadTotal productos):", style = MaterialTheme.typography.bodyLarge)
                                Text(formatoMoneda.format(total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val direccion = if (usarDireccionRegistrada) user?.direccion ?: "" else direccionPersonalizada
                                    if (esInvitado) {
                                        if (nombreInvitado.isBlank()) { Toast.makeText(context, "Ingresa tu nombre", Toast.LENGTH_SHORT).show(); return@Button }
                                        if (correoInvitado.isBlank() || !isValidEmail(correoInvitado)) { Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show(); return@Button }
                                        if (telefonoInvitado.length != 9) { Toast.makeText(context, "Teléfono debe ser de 9 dígitos", Toast.LENGTH_SHORT).show(); return@Button }
                                    }
                                    if (direccion.isBlank() || fechaEntrega.isBlank()) {
                                        Toast.makeText(context, "Completa fecha y dirección", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    carritoViewModel.confirmarPedido(
                                        uid = currentUid,
                                        fechaEntrega = fechaEntrega,
                                        direccion = direccion,
                                        nombre = if (esInvitado) nombreInvitado else user?.nombre ?: "",
                                        correo = if (esInvitado) correoInvitado else user?.correo ?: "",
                                        telefono = if (esInvitado) telefonoInvitado else user?.telefono ?: "",
                                        onStockDescontar = { n, c -> drawerMenuViewModel.actualizarStock(n, c) }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("PAGAR AHORA", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        ) { padding ->
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("Tu carrito está vacío", color = Color.Gray)
                        TextButton(onClick = { navController.navigate(AppRoutes.CATALOGO) }) { Text("Ir a comprar") }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(items) { item ->
                        CarritoItemRow(item, carritoViewModel, currentUid)
                    }
                    item {
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                        if (esInvitado) {
                            Text("Datos de Contacto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            OutlinedTextField(value = nombreInvitado, onValueChange = { nombreInvitado = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp))
                            OutlinedTextField(value = correoInvitado, onValueChange = { correoInvitado = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp))
                            OutlinedTextField(value = telefonoInvitado, onValueChange = { if (it.length <= 9) telefonoInvitado = it.filter { c -> c.isDigit() } }, label = { Text("Teléfono (9 dígitos)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp))
                        }
                        Text("Entrega", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                        OutlinedTextField(value = fechaEntrega, onValueChange = {}, readOnly = true, label = { Text("Fecha de entrega") }, trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Default.DateRange, null) } }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp))
                        if (!esInvitado) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                Checkbox(checked = usarDireccionRegistrada, onCheckedChange = { usarDireccionRegistrada = it })
                                Text("Usar mi dirección registrada")
                            }
                        }
                        if (!usarDireccionRegistrada || esInvitado) {
                            OutlinedTextField(value = direccionPersonalizada, onValueChange = { direccionPersonalizada = it }, label = { Text("Dirección de entrega") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp))
                        }
                        Spacer(Modifier.height(120.dp))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        fechaEntrega = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                        showDatePicker = false
                    }
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun CarritoItemRow(item: CarritoItemEntity, vm: CarritoViewModel, uid: String) {
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(item.imagenResId), contentDescription = null, modifier = Modifier.size(70.dp).background(Color.White, RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreProducto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(formatoMoneda.format(item.precioUnitario), color = MaterialTheme.colorScheme.primary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { vm.cambiarCantidad(uid, item.id, item.cantidad - 1) }) { Icon(Icons.Default.RemoveCircleOutline, null) }
                Text("${item.cantidad}", fontWeight = FontWeight.Bold)
                IconButton(onClick = { vm.cambiarCantidad(uid, item.id, item.cantidad + 1) }) { Icon(Icons.Default.AddCircleOutline, null) }
                IconButton(onClick = { vm.eliminarItem(uid, item.id) }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            }
        }
    }
}
