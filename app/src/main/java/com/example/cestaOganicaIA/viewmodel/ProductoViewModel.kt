package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Esta clase se encargará de la lógica de negocio de los productos.
// Por ahora, solo simulará que añade un producto.
class ProductoViewModel : ViewModel() {

    /**
     * Simula el proceso de añadir un producto a un carrito o a una lista de pedidos.
     * En una app real, aquí es donde llamarías a un Repositorio para guardar
     * esta información en una base de datos (como Room) o enviarla a un servidor.
     *
     * Usamos viewModelScope.launch para indicar que esta podría ser una operación
     * larga (como guardar en base de datos) y no debe bloquear la interfaz de usuario.
     *
     * @param nombre El nombre del producto a añadir.
     * @param cantidad La cantidad de unidades del producto.
     * @param direccion La dirección de entrega para este pedido.
     */
    fun agregarProducto(nombre: String, cantidad: Int, direccion: String) {
        viewModelScope.launch {
            // --- Lógica futura ---a
            // Aquí es donde en el futuro podrías:
            // 1. Crear un objeto "PedidoItem".
            // 2. Llamar a `repository.insertarPedido(pedidoItem)`.
            // 3. Actualizar el stock, etc.

            // Por ahora, solo imprimimos en la consola para saber que funciona.
            println("PRODUCTO AÑADIDO: $cantidad x $nombre, para entregar en: $direccion")
        }
    }
}
