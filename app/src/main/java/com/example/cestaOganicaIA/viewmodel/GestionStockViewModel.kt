package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.R
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestionStockViewModel(private val repository: ProductoRepository) : ViewModel() {

    val productos: StateFlow<List<Producto>> = repository.obtenerProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.refrescar() }
    }

    fun preCargarProductos() {
        viewModelScope.launch {
            val iniciales = listOf(
                Producto(nombre = "Manzana Fuji", precio = "1500", stock = 50, descripcion = "Manzanas frescas y crujientes.", imagenResId = R.drawable.manzana_fuji, categoria = "Frutas"),
                Producto(nombre = "Plátano", precio = "1200", stock = 40, descripcion = "Plátanos maduros.", imagenResId = R.drawable.platano_cavendish, categoria = "Frutas"),
                Producto(nombre = "Zanahoria", precio = "800", stock = 100, descripcion = "Zanahorias orgánicas.", imagenResId = R.drawable.zanahorias, categoria = "Verduras"),
                Producto(nombre = "Espinaca", precio = "1000", stock = 30, descripcion = "Espinaca fresca por atado.", imagenResId = R.drawable.espinaca, categoria = "Verduras"),
                Producto(nombre = "Miel Orgánica", precio = "5500", stock = 15, descripcion = "Miel pura de abeja.", imagenResId = R.drawable.miel_organica, categoria = "Orgánicos"),
                Producto(nombre = "Pimientos", precio = "1200", stock = 20, descripcion = "Pimientos rojos y verdes.", imagenResId = R.drawable.pimientos, categoria = "Verduras"),
                Producto(nombre = "Naranja Valencia", precio = "1800", stock = 35, descripcion = "Naranjas jugosas para jugo.", imagenResId = R.drawable.naranja_valencia, categoria = "Frutas"),
                Producto(nombre = "Quinua Orgánica", precio = "3200", stock = 25, descripcion = "Quinua de alta calidad.", imagenResId = R.drawable.quinua_organica, categoria = "Orgánicos")
            )
            iniciales.forEach { repository.insertarProducto(it) }
        }
    }

    fun agregarProducto(nombre: String, precio: String, stock: Int, descripcion: String, categoria: String, imagenResId: Int, imagenUri: String? = null) {
        viewModelScope.launch {
            repository.insertarProducto(
                Producto(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    descripcion = descripcion,
                    categoria = categoria,
                    imagenResId = imagenResId,
                    imagenUri = imagenUri
                )
            )
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.actualizarProducto(producto)
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.eliminarProducto(producto)
        }
    }

    fun limpiarTodo() {
        viewModelScope.launch {
            repository.eliminarTodo()
        }
    }

    class Factory(private val repository: ProductoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = GestionStockViewModel(repository) as T
    }
}