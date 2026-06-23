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

    // NUEVO: estado de error para mostrar al usuario en vez de crashear
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // NUEVO: estado de carga mientras se confirma el pedido
    private val _procesandoPago = MutableStateFlow(false)
    val procesandoPago: StateFlow<Boolean> = _procesandoPago.asStateFlow()

    fun limpiarError() { _error.value = null }

    fun cargarCarrito(uid: String) {
        viewModelScope.launch {
            carritoRepo.itemsDeUsuario(uid)
                .catch { e -> _error.value = "No se pudo cargar el carrito: ${e.message}" }
                .collect { _items.value = it }
        }
    }

    fun agregarAlCarrito(uid: String, nombre: String, precio: Double, imagenResId: Int, cantidad: Int) {
        viewModelScope.launch {
            try {
                carritoRepo.agregarOActualizar(uid, nombre, precio, imagenResId, cantidad)
            } catch (e: Exception) {
                _error.value = "No se pudo agregar el producto: ${e.message}"
            }
        }
    }

    fun cambiarCantidad(uid: String, itemLocalId: Int, nuevaCantidad: Int) {
        viewModelScope.launch {
            try {
                val item = _items.value.find { it.idLocal == itemLocalId }
                if (item != null) {
                    if (nuevaCantidad > 0) {
                        carritoRepo.cambiarCantidad(item, nuevaCantidad)
                    } else {
                        carritoRepo.eliminarItem(item)
                    }
                }
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar la cantidad: ${e.message}"
            }
        }
    }

    fun eliminarItem(uid: String, itemLocalId: Int) {
        viewModelScope.launch {
            try {
                val item = _items.value.find { it.idLocal == itemLocalId }
                if (item != null) {
                    carritoRepo.eliminarItem(item)
                }
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar el producto: ${e.message}"
            }
        }
    }

    fun vaciarCarrito(uid: String) {
        viewModelScope.launch {
            try {
                carritoRepo.vaciarCarrito(uid)
            } catch (e: Exception) {
                _error.value = "No se pudo vaciar el carrito: ${e.message}"
            }
        }
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
            _procesandoPago.value = true
            try {
                val ordenId = UUID.randomUUID().toString().take(8).uppercase()
                val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                // Copia local de los items: evita que vaciarCarrito() altere la lista
                // mientras todavía estamos iterando sobre ella (causa de bugs intermitentes)
                val itemsAComprar = _items.value.toList()

                itemsAComprar.forEach { item ->
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
                    productoRepo.descontarStock(item.nombreProducto, item.cantidad)
                    onStockDescontar(item.nombreProducto, item.cantidad)
                }
                carritoRepo.vaciarCarrito(uid)
                _pedidoConfirmado.value = true
            } catch (e: Exception) {
                // Antes: esto no se atrapaba y la app se cerraba después de "pagar"
                _error.value = "No se pudo completar la compra: ${e.message ?: "error desconocido"}"
            } finally {
                _procesandoPago.value = false
            }
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
            _procesandoPago.value = true
            try {
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
                productoRepo.descontarStock(nombreProducto, cantidad)
                onStockDescontar(nombreProducto, cantidad)
                _pedidoConfirmado.value = true
            } catch (e: Exception) {
                _error.value = "No se pudo completar la compra: ${e.message ?: "error desconocido"}"
            } finally {
                _procesandoPago.value = false
            }
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