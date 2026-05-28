package com.example.cestaOganicaIA.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cestaOganicaIA.data.model.Categoria
import com.example.cestaOganicaIA.data.model.CatalogoItem
import com.example.cestaOganicaIA.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// Define la lista ORIGINAL fuera, como una constante privada. Esta es la "fuente de la verdad" inicial.
private val listaInicial = listOf(
    Categoria("Frutas", Icons.Default.Agriculture, listOf(
        CatalogoItem("Manzanas Fuji", 1200, "Manzanas Fuji crujientes y dulces...", 50, R.drawable.manzana_fuji, "Frutas"),
        CatalogoItem("Naranjas Valencia", 1000, "Jugosas y ricas en vitamina C...", 30, R.drawable.naranja_valencia, "Frutas"),
        CatalogoItem("Plátanos Cavendish", 800, "Plátanos maduros y dulces...", 100, R.drawable.platano_cavendish, "Frutas")
    )),
    Categoria("Verduras", Icons.Default.Grass, listOf(
        CatalogoItem("Zanahorias Orgánicas", 900, "Zanahorias crujientes cultivadas sin pesticidas...", 40, R.drawable.zanahorias, "Verduras"),
        CatalogoItem("Espinacas Frescas", 700, "Espinacas frescas y nutritivas...", 25, R.drawable.espinaca, "Verduras"),
        CatalogoItem("Pimientos Tricolores", 1500, "Pimientos rojos, amarillos y verdes...", 20, R.drawable.pimientos, "Verduras")
    )),
    Categoria("Orgánicos", Icons.Default.Eco, listOf(
        CatalogoItem("Miel Orgánica", 5000, "Miel pura y orgánica producida por apicultores locales...", 15, R.drawable.miel_organica, "Orgánicos"),
        CatalogoItem("Quinua Orgánica", 5050, "Superalimento rico en proteínas y fibra, libre de gluten.", 35, R.drawable.quinua_organica, "Orgánicos")
    )),
    Categoria("Lácteos", Icons.Default.Icecream, listOf(
        CatalogoItem("Leche Entera", 1500, "Leche fresca y cremosa de vacas de pastoreo libre.", 40, R.drawable.leche_entera, "Lácteos")
    ))
)

class DrawerMenuViewModel : ViewModel() {
    // El ViewModel expone el estado de las categorías.
    // Solo este ViewModel puede modificarlo internamente.
    var categorias = mutableStateOf(listaInicial)
        private set

    companion object {
        // Este objeto singleton permite que otras partes de la app (como ProductoFormScreen)
        // accedan a la instancia ÚNICA de este ViewModel para darle órdenes.
        var instance: DrawerMenuViewModel? = null
    }

    init {
        // En cuanto el ViewModel se crea por primera vez, se registra a sí mismo en el singleton.
        instance = this
    }

    /**
     * Busca un producto por su nombre y actualiza su stock.
     * Esta función es la ÚNICA que debe modificar el estado de las categorías.
     */
    fun actualizarStock(nombreProducto: String, cantidadComprada: Int) {
        // Creamos una lista completamente nueva a partir de la anterior
        val listaActualizada = categorias.value.map { categoria ->
            // Para cada categoría, creamos una copia
            categoria.copy(
                // Y para su lista de productos, creamos otra lista nueva
                productos = categoria.productos.map { producto ->
                    // Si encontramos el producto que se compró...
                    if (producto.nombre == nombreProducto) {
                        // ...creamos una copia de ESE producto con el stock actualizado
                        producto.copy(stock = producto.stock - cantidadComprada)
                    } else {
                        // ...si no es, lo dejamos como está.
                        producto
                    }
                }
            )
        }
        // Finalmente, actualizamos el estado con la lista completamente nueva.
        // Compose detectará este cambio y redibujará la UI que depende de él.
        categorias.value = listaActualizada
    }
}
