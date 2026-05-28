package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.CarritoItemEntity
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CarritoViewModel(
    private val carritoRepo: CarritoRepository,
    private val pedidoRepo: PedidoRepository,
    private val productoRepo: ProductoRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CarritoItemEntity>>(emptyList())
    val items: StateFlow<List<CarritoItemEntity>> = _items.asStateFlow()

    val total: StateFlow<Double> = _items.map { lista ->
        lista.sumOf { it.precioUnitario * it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val cantidadTotal: StateFlow<Int> = _items.map { lista ->
        lista.sumOf { it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _pedidoConfirmado = MutableStateFlow(false)
    val pedidoConfirmado: StateFlow<Boolean> = _pedidoConfirmado.asStateFlow()

    fun cargarCarrito(uid: String) {
        viewModelScope.launch {
            carritoRepo.itemsDeUsuario(uid).collect { _items.value = it }
        }
    }

    fun agregarAlCarrito(uid: String, nombre: String, precio: Double, imagenResId: Int, cantidad: Int) {
        viewModelScope.launch {
            carritoRepo.agregarOActualizar(uid, nombre, precio, imagenResId, cantidad)
        }
    }

    fun cambiarCantidad(uid: String, itemLocalId: Int, nuevaCantidad: Int) {
        viewModelScope.launch {
            // Buscamos específicamente por el ID único de la base de datos local
            val item = _items.value.find { it.idLocal == itemLocalId }
            if (item != null) {
                if (nuevaCantidad > 0) {
                    carritoRepo.cambiarCantidad(item, nuevaCantidad)
                } else {
                    carritoRepo.eliminarItem(item)
                }
            }
        }
    }

    fun eliminarItem(uid: String, itemLocalId: Int) {
        viewModelScope.launch {
            val item = _items.value.find { it.idLocal == itemLocalId }
            if (item != null) {
                carritoRepo.eliminarItem(item)
            }
        }
    }

    fun vaciarCarrito(uid: String) {
        viewModelScope.launch { carritoRepo.vaciarCarrito(uid) }
    }

    fun confirmarPedido(
        uid: String,
        fechaEntrega: String,
        direccion: String,
        nombre: String,
        correo: String,
        telefono: String,
        onStockDescontar: (String, Int) -> Unit
    ) {
        viewModelScope.launch {
            val ordenId = UUID.randomUUID().toString().take(8).uppercase()
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            
            _items.value.forEach { item ->
                pedidoRepo.confirmarPedido(
                    PedidoEntity(
                        ordenId = ordenId,
                        usuarioId = uid,
                        nombreContacto = nombre,
                        correoContacto = correo,
                        nombreProducto = item.nombreProducto,
                        precioUnitario = item.precioUnitario.toInt(),
                        cantidad = item.cantidad,
                        total = (item.precioUnitario * item.cantidad).toInt(),
                        imagenResId = item.imagenResId,
                        fechaEntrega = fechaEntrega,
                        direccionEntrega = direccion,
                        fechaPedido = fechaActual,
                        estado = "Confirmado"
                    )
                )
                // Descontar de la base de datos Room
                productoRepo.descontarStock(item.nombreProducto, item.cantidad)
                // Mantener compatibilidad con el callback si se usa para otros estados en memoria
                onStockDescontar(item.nombreProducto, item.cantidad)
            }
            carritoRepo.vaciarCarrito(uid)
            _pedidoConfirmado.value = true
        }
    }

    fun confirmarCompraDirecta(
        uid: String,
        nombreProducto: String,
        precio: Double,
        cantidad: Int,
        imagenResId: Int,
        fechaEntrega: String,
        direccion: String,
        nombreContacto: String,
        correoContacto: String,
        telefonoContacto: String,
        onStockDescontar: (String, Int) -> Unit
    ) {
        viewModelScope.launch {
            val ordenId = UUID.randomUUID().toString().take(8).uppercase()
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            
            pedidoRepo.confirmarPedido(
                PedidoEntity(
                    ordenId = ordenId,
                    usuarioId = uid,
                    nombreContacto = nombreContacto,
                    correoContacto = correoContacto,
                    nombreProducto = nombreProducto,
                    precioUnitario = precio.toInt(),
                    cantidad = cantidad,
                    total = (precio * cantidad).toInt(),
                    imagenResId = imagenResId,
                    fechaEntrega = fechaEntrega,
                    direccionEntrega = direccion,
                    fechaPedido = fechaActual,
                    estado = "Confirmado"
                )
            )
            // Descontar de Room
            productoRepo.descontarStock(nombreProducto, cantidad)
            onStockDescontar(nombreProducto, cantidad)
            _pedidoConfirmado.value = true
        }
    }

    fun resetPedidoConfirmado() { _pedidoConfirmado.value = false }

    class Factory(
        private val carritoRepo: CarritoRepository,
        private val pedidoRepo: PedidoRepository,
        private val productoRepo: ProductoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CarritoViewModel(carritoRepo, pedidoRepo, productoRepo) as T
    }
}
