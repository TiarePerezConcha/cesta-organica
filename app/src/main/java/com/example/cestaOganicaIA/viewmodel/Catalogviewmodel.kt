package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.R
import com.example.cestaOganicaIA.data.model.CatalogoItem
import com.example.cestaOganicaIA.data.model.Categoria
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.repository.CarritoRepository
import com.example.cestaOganicaIA.data.repository.FavoritoRepository
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val carritoRepo: CarritoRepository,
    private val favoritoRepo: FavoritoRepository,
    private val productoRepo: ProductoRepository
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _favoritosNombres = MutableStateFlow<Set<String>>(emptySet())
    val favoritosNombres: StateFlow<Set<String>> = _favoritosNombres.asStateFlow()

    private val _carritoCount = MutableStateFlow(0)
    val carritoCount: StateFlow<Int> = _carritoCount.asStateFlow()

    private var userJob: Job? = null

    private val availableIcons = listOf(
        Icons.Default.Eco,
        Icons.Default.Agriculture,
        Icons.Default.Grass,
        Icons.Default.ShoppingBasket,
        Icons.Default.Store,
        Icons.Default.Nature,
        Icons.Default.NaturePeople,
        Icons.Default.LocalFlorist,
        Icons.Default.Spa,
        Icons.Default.Compost,
        Icons.Default.Yard,
        Icons.Default.Inventory,
        Icons.Default.LocalMall
    )

    init {
        observarProductos()
        viewModelScope.launch { productoRepo.refrescar() }
    }

    private fun getIconForCategory(name: String): ImageVector {
        return when (name) {
            "Frutas" -> Icons.Default.Agriculture
            "Verduras" -> Icons.Default.Grass
            "Orgánicos" -> Icons.Default.Eco
            else -> {
                val index = name.hashCode().let { if (it < 0) -it else it } % availableIcons.size
                availableIcons[index]
            }
        }
    }

    private fun observarProductos() {
        viewModelScope.launch {
            productoRepo.obtenerProductos().collect { productos ->
                val grouped = productos.groupBy { it.categoria }.map { (nombreCat, listaProds) ->
                    Categoria(
                        nombre = nombreCat,
                        icono = getIconForCategory(nombreCat),
                        productos = listaProds.map { 
                            CatalogoItem(it.nombre, it.precio.toIntOrNull() ?: 0, it.descripcion, it.stock, it.imagenResId, it.categoria)
                        }
                    )
                }
                _categorias.value = grouped
            }
        }
    }

    fun preCargarProductos() {
        viewModelScope.launch {
            val iniciales = listOf(
                Producto(nombre = "Manzana Fuji", precio = "1500", stock = 50, descripcion = "Manzanas frescas y crujientes.", imagenResId = R.drawable.manzana_fuji, categoria = "Frutas"),
                Producto(nombre = "Plátano", precio = "1200", stock = 40, descripcion = "Plátanos maduros.", imagenResId = R.drawable.platano_cavendish, categoria = "Frutas"),
                Producto(nombre = "Zanahoria", precio = "800", stock = 100, descripcion = "Zanahorias orgánicas.", imagenResId = R.drawable.zanahorias, categoria = "Verduras"),
                Producto(nombre = "Espinaca", precio = "1000", stock = 30, descripcion = "Espinaca fresca por atado.", imagenResId = R.drawable.espinaca, categoria = "Verduras"),
                Producto(nombre = "Miel Orgánica", precio = "5500", stock = 15, descripcion = "Miel pura de abeja.", imagenResId = R.drawable.miel_organica, categoria = "Orgánicos"),
                Producto(nombre = "Pimientos", precio = "1200", stock = 20, descripcion = "Pimientos de colores.", imagenResId = R.drawable.pimientos, categoria = "Verduras"),
                Producto(nombre = "Naranja Valencia", precio = "1800", stock = 35, descripcion = "Naranjas para jugo.", imagenResId = R.drawable.naranja_valencia, categoria = "Frutas"),
                Producto(nombre = "Quinua Orgánica", precio = "3200", stock = 25, descripcion = "Quinua blanca orgánica.", imagenResId = R.drawable.quinua_organica, categoria = "Orgánicos")
            )
            iniciales.forEach { productoRepo.insertarProducto(it) }
        }
    }

    fun cargarDatosUsuario(uid: String) {
        userJob?.cancel()
        if (uid.isEmpty()) {
            _favoritosNombres.value = emptySet()
            _carritoCount.value = 0
            return
        }
        userJob = viewModelScope.launch {
            launch {
                favoritoRepo.favoritosDeUsuario(uid).collect { lista ->
                    _favoritosNombres.value = lista.map { it.nombreProducto }.toSet()
                }
            }
            launch {
                carritoRepo.itemsDeUsuario(uid).collect { items ->
                    _carritoCount.value = items.sumOf { it.cantidad }
                }
            }
        }
    }

    fun toggleFavorito(uid: String, nombreProducto: String) {
        viewModelScope.launch {
            favoritoRepo.toggleFavorito(uid, nombreProducto)
        }
    }

    class Factory(
        private val carritoRepo: CarritoRepository,
        private val favoritoRepo: FavoritoRepository,
        private val productoRepo: ProductoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CatalogViewModel(carritoRepo, favoritoRepo, productoRepo) as T
    }
}
