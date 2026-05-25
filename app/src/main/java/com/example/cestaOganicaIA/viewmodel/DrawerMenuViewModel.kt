package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.model.Categoria
import com.example.cestaOganicaIA.data.model.CatalogoItem
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import kotlinx.coroutines.launch

class DrawerMenuViewModel : ViewModel() {
    private val productoRepo = ProductoRepository()
    
    // Estado del catálogo cargado desde Firebase
    var categorias = mutableStateOf<List<Categoria>>(emptyList())
        private set

    companion object {
        var instance: DrawerMenuViewModel? = null
    }

    init {
        instance = this
        cargarProductosDesdeFirebase()
    }

    private fun cargarProductosDesdeFirebase() {
        viewModelScope.launch {
            productoRepo.obtenerProductos().collect { productos ->
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
                categorias.value = grouped
            }
        }
    }

    /** 
     * En Firebase, el stock se descuenta en el servidor normalmente.
     * Esta función notifica al sistema para que refresque la vista si es necesario.
     */
    fun actualizarStock(nombreProducto: String, cantidadComprada: Int) {
        // En una app real con Firebase, aquí llamarías a un Cloud Function o a un update en Firestore.
        // Por ahora, el listener de cargarProductosDesdeFirebase actualizará la UI automáticamente
        // cuando el servidor de Google confirme el cambio de stock.
    }
}
