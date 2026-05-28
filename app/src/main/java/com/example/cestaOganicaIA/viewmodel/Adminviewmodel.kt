package com.example.cestaOganicaIA.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.AppDatabase
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val pedidoDao = database.pedidoDao()
    private val productoDao = database.productoDao()
    private val productoRepo = ProductoRepository(productoDao)

    val todosPedidos: StateFlow<List<PedidoEntity>> = pedidoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Actualiza el estado de una orden completa */
    fun actualizarEstadoOrden(ordenId: String, nuevoEstado: String) {
        viewModelScope.launch {
            pedidoDao.actualizarEstadoOrden(ordenId, nuevoEstado)
        }
    }

    /** Agrega un producto manualmente a la BBDD local */
    fun agregarProductoManual(producto: Producto) {
        viewModelScope.launch {
            productoRepo.insertarProducto(producto)
        }
    }

    /** Carga catálogo inicial si está vacío */
    fun cargarCatalogoInicial() {
        val lista = listOf(
            Producto(nombre = "Manzanas Fuji", precio = "1200", stock = 50, categoria = "Frutas", imagenResId = R.drawable.manzana_fuji),
            Producto(nombre = "Naranjas Valencia", precio = "1000", stock = 30, categoria = "Frutas", imagenResId = R.drawable.naranja_valencia)
        )
        viewModelScope.launch {
            lista.forEach { productoRepo.insertarProducto(it) }
        }
    }

    /** Gestión de usuarios */
    fun refrescarUsuarios() {
        // Implementar carga desde Firebase si es necesario
    }
}
