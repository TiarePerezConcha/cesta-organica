package com.example.cestaOganicaIA.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.cestaOganicaIA.data.database.PedidoHistorial
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.model.Resena
import com.example.cestaOganicaIA.data.repository.ResenaRepository
import com.example.cestaOganicaIA.data.session.SessionManager
import com.example.cestaOganicaIA.viewmodel.DrawerMenuViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.cestaOganicaIA.viewmodel.CarritoViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoFormScreen(
    navController: NavController,
    nombre: String,
    precio: String,
    descripcion: String,
    stock: Int,
    imagenResId: Int,
    carritoViewModel: CarritoViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // Para manejar las funciones suspendidas de la base de datos
    val usuarioActual = SessionManager.currentUser

    var cantidad by remember { mutableStateOf("1") }
    var fechaEntrega by remember { mutableStateOf("") }
    var mostrarDialogoBoleta by remember { mutableStateOf(false) }
    var mostrarDialogoFecha by remember { mutableStateOf(false) }
    var mostrarDialogoResena by remember { mutableStateOf(false) }

    // Estado para la lista de reseñas
    var resenas by remember { mutableStateOf<List<Resena>>(emptyList()) }

    // Carga inicial de reseñas de forma asíncrona
    LaunchedEffect(nombre) {
        resenas = ResenaRepository.obtenerResenasPorProducto(nombre)
    }

    val estadoFecha = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    // Limpiamos el string de precio por si trae símbolos de moneda
    val precioBase = precio.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
    val cantidadNum = cantidad.toIntOrNull() ?: 0
    val total = precioBase * cantidadNum
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = nombre, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = "Precio Unitario: ${formatoMoneda.format(precioBase)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Stock disponible: $stock unidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Justify
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() }
                            cantidad = if (filtered.startsWith("0") && filtered.length > 1) filtered.substring(1) else filtered
                        },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fechaEntrega,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de Entrega") },
                        placeholder = { Text("Selecciona una fecha") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.clickable { mostrarDialogoFecha = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Total a Pagar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = formatoMoneda.format(total),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (cantidad.isBlank() || cantidadNum <= 0 || fechaEntrega.isBlank()) {
                                Toast.makeText(context, "Completa los datos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (cantidadNum > stock) {
                                Toast.makeText(context, "Stock insuficiente", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            mostrarDialogoBoleta = true
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Comprar Ahora")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                }
            }

            // SECCIÓN DE RESEÑAS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reseñas y Calificaciones", style = MaterialTheme.typography.titleLarge)
                    if (usuarioActual != null) {
                        IconButton(onClick = { mostrarDialogoResena = true }) {
                            Icon(Icons.Default.AddComment, "Añadir Reseña")
                        }
                    }
                }
            }

            if (resenas.isEmpty()) {
                item {
                    Text("Sin reseñas aún.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(resenas) { resena ->
                    CardResena(resena)
                }
            }}}}