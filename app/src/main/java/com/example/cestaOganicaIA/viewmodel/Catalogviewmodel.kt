package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.model.CatalogoItem
import com.example.cestaOganicaIA.data.model.Categoria
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.FavoritoRepository
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val carritoRepo: CarritoRepository,
    private val favoritoRepo: FavoritoRepository,
    private val productoRepo: ProductoRepository = ProductoRepository()
) : ViewModel() {

    // Estado del catálogo cargado desde Firestore
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _favoritosNombres = MutableStateFlow<Set<String>>(emptySet())
    val favoritosNombres: StateFlow<Set<String>> = _favoritosNombres.asStateFlow()

    private val _carritoCount = MutableStateFlow(0)
    val carritoCount: StateFlow<Int> = _carritoCount.asStateFlow()

    init {
        observarProductos()
    }

    private fun observarProductos() {
        viewModelScope.launch {
            productoRepo.obtenerProductos().collect { productos ->
                // Agrupamos los productos de Firestore por su campo "categoria"
                val grouped = productos.groupBy { it.categoria }.map { (nombreCat, listaProds) ->
                    Categoria(
                        nombre = nombreCat,
                        icono = when(nombreCat) {
                            "Frutas" -> Icons.Default.Agriculture
                            "Verduras" -> Icons.Default.Grass
                            "Orgánicos" -> Icons.Default.Eco
                            else -> Icons.Default.ShoppingBasket
                        },
                        productos = listaProds.map { 
                            CatalogoItem(it.nombre, it.precio.toIntOrNull() ?: 0, it.descripcion, it.stock, it.imagenResId, it.categoria)
                        }
                    )
                }
                _categorias.value = grouped
            }
        }
    }

    fun cargarDatosUsuario(uid: String) {
        viewModelScope.launch {
            favoritoRepo.favoritosDeUsuario(uid).collect { lista ->
                _favoritosNombres.value = lista.map { it.nombreProducto }.toSet()
            }
        }
        viewModelScope.launch {
            carritoRepo.itemsDeUsuario(uid).collect { items ->
                _carritoCount.value = items.sumOf { it.cantidad }
            }
        }
    }

    fun toggleFavorito(uid: String, nombreProducto: String) {
        viewModelScope.launch {
            favoritoRepo.toggleFavorito(uid, nombreProducto)
        }
    }

    fun agregarAlCarrito(uid: String, item: CatalogoItem, cantidad: Int) {
        viewModelScope.launch {
            carritoRepo.agregarOActualizar(uid, item.nombre, item.precio, item.imagenResId, cantidad)
        }
    }

    class Factory(
        private val carritoRepo: CarritoRepository,
        private val favoritoRepo: FavoritoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CatalogViewModel(carritoRepo, favoritoRepo) as T
    }
}
