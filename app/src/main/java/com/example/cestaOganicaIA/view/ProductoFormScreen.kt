package com.example.cestaOganicaIA.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cestaOganicaIA.data.repository.ResenaRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.ui.shared.AppRoutes
import com.example.cestaOganicaIA.ui.shared.HuertoScaffold
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.viewmodel.CarritoViewModel
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    navController: NavController,
    nombre: String,
    precio: String,
    descripcion: String,
    stock: Int,
    imagenResId: Int,
    carritoViewModel: CarritoViewModel,
    drawerMenuViewModel: DrawerMenuViewModel
) {
    val context = LocalContext.current
    val usuarioActual = SessionManager.currentUser
    val scope = rememberCoroutineScope()

    var cantidad by remember { mutableStateOf("1") }
    var fechaEntrega by remember { mutableStateOf("") }
    var mostrarDialogoPago by remember { mutableStateOf(false) }
    var mostrarDialogoExito by remember { mutableStateOf(false) }
    var mostrarDialogoFecha by remember { mutableStateOf(false) }

    val resenas by remember { mutableStateOf(ResenaRepository.obtenerResenasPorProducto(nombre)) }
    
    val estadoFecha = rememberDatePickerState(
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

    val precioBase = precio.toDoubleOrNull() ?: 0.0
    val cantidadNum = cantidad.toIntOrNull() ?: 0
    val total = precioBase * cantidadNum
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    HuertoHogarTheme {
        HuertoScaffold(
            titulo = "Detalle de Producto",
            navController = navController,
            onBack = { navController.popBackStack() }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Precio: ${formatoMoneda.format(precioBase)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Stock: $stock unidades", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(text = descripcion, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Justify)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OutlinedTextField(
                            value = cantidad,
                            onValueChange = { cantidad = it.filter { c -> c.isDigit() } },
                            label = { Text("Cantidad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = fechaEntrega,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha de Entrega") },
                            placeholder = { Text("Selecciona una fecha") },
                            trailingIcon = {
                                IconButton(onClick = { mostrarDialogoFecha = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().clickable { mostrarDialogoFecha = true }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text("Total a Pagar", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = formatoMoneda.format(total),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    if (cantidadNum <= 0) {
                                        Toast.makeText(context, "Ingresa una cantidad válida", Toast.LENGTH_SHORT).show()
                                    } else if (cantidadNum > stock) {
                                        Toast.makeText(context, "No hay suficiente stock", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val uid = usuarioActual?.uid ?: "INVITADO"
                                        carritoViewModel.agregarAlCarrito(uid, nombre, precioBase, imagenResId, cantidadNum)
                                        Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("AGREGAR AL CARRITO")
                            }
                            
                            Button(
                                onClick = {
                                    if (cantidadNum <= 0 || fechaEntrega.isEmpty()) {
                                        Toast.makeText(context, "Completa la cantidad y la fecha", Toast.LENGTH_SHORT).show()
                                    } else if (cantidadNum > stock) {
                                        Toast.makeText(context, "No hay suficiente stock", Toast.LENGTH_LONG).show()
                                    } else {
                                        mostrarDialogoPago = true
                                        scope.launch {
                                            delay(2000)
                                            mostrarDialogoPago = false
                                            
                                            val uid = usuarioActual?.uid ?: "INVITADO"
                                            val direccion = usuarioActual?.direccion ?: "Dirección de prueba"
                                            val nom = usuarioActual?.nombre ?: "Usuario"
                                            val mail = usuarioActual?.correo ?: "correo@prueba.com"
                                            val tel = usuarioActual?.telefono ?: "999999999"

                                            carritoViewModel.confirmarCompraDirecta(
                                                uid = uid,
                                                nombreProducto = nombre,
                                                precio = precioBase,
                                                cantidad = cantidadNum,
                                                imagenResId = imagenResId,
                                                fechaEntrega = fechaEntrega,
                                                direccion = direccion,
                                                nombreContacto = nom,
                                                correoContacto = mail,
                                                telefonoContacto = tel,
                                                onStockDescontar = { n, c -> drawerMenuViewModel.actualizarStock(n, c) }
                                            )
                                            
                                            mostrarDialogoExito = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("COMPRAR AHORA")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Reseñas y Calificaciones", style = MaterialTheme.typography.titleLarge)
                        if (usuarioActual != null) {
                            IconButton(onClick = { /* Futuro */ }) {
                                Icon(Icons.Default.AddComment, "Añadir Reseña")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (resenas.isEmpty()) {
                    item { Text("Sin reseñas aún.", color = Color.Gray, modifier = Modifier.padding(16.dp)) }
                } else {
                    items(resenas) { resena ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text(resena.nombreUsuario, fontWeight = FontWeight.Bold)
                                Text(resena.comentario)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarDialogoFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoFecha.selectedDateMillis?.let {
                        fechaEntrega = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }
                    mostrarDialogoFecha = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = estadoFecha) }
    }

    if (mostrarDialogoPago) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Procesando Pago") },
            text = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Redirigiendo a plataforma de pago...")
                }
            },
            confirmButton = {}
        )
    }

    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExito = false },
            title = { Text("¡Compra Exitosa!") },
            text = { Text("Gracias por tu compra. Tu pedido ha sido registrado con éxito.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoExito = false
                    navController.navigate(AppRoutes.HISTORIAL)
                }) { Text("Ir al Historial") }
            }
        )
    }
}
