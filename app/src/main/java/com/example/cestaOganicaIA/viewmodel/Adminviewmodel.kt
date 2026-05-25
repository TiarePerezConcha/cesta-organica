package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.model.Credential
import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import com.example.cestaOganicaIA.data.repository.ProductoRepository
import com.example.cestaOganicaIA.data.repository.UserRepository
import com.example.cestaOganicaIA.R
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel(
    private val pedidoRepo: PedidoRepository
) : ViewModel() {

    private val productoRepo = ProductoRepository()

    private val _usuarios = MutableStateFlow<List<Credential>>(emptyList())
    val usuarios: StateFlow<List<Credential>> = _usuarios.asStateFlow()

    private val _todosPedidos = MutableStateFlow<List<PedidoEntity>>(emptyList())
    val todosPedidos: StateFlow<List<PedidoEntity>> = _todosPedidos.asStateFlow()

    init {
        refrescarUsuarios()
        pedidoRepo.todosPedidos()
            .onEach { _todosPedidos.value = it }
            .launchIn(viewModelScope)
    }

    /** 
     * Función para subir el catálogo inicial a Firebase de forma inteligente.
     * Verifica si el producto ya existe (por nombre) para no duplicarlo.
     */
    fun cargarCatalogoInicial() {
        val listaLocal = listOf(
            // FRUTAS
            Producto(nombre = "Manzanas Fuji", precio = "1200", stock = 50, categoria = "Frutas", imagenResId = R.drawable.manzana_fuji, descripcion = "Manzanas Fuji crujientes y dulces, cosechadas en plena temporada."),
            Producto(nombre = "Naranjas Valencia", precio = "1000", stock = 30, categoria = "Frutas", imagenResId = R.drawable.naranja_valencia, descripcion = "Jugosas y ricas en vitamina C, perfectas para jugo natural."),
            Producto(nombre = "Plátanos Cavendish", precio = "800", stock = 100, categoria = "Frutas", imagenResId = R.drawable.platano_cavendish, descripcion = "Plátanos maduros y dulces, ideales para el desayuno."),
            
            // VERDURAS
            Producto(nombre = "Zanahorias Orgánicas", precio = "900", stock = 40, categoria = "Verduras", imagenResId = R.drawable.zanahorias, descripcion = "Zanahorias crujientes cultivadas sin pesticidas, directas del campo."),
            Producto(nombre = "Espinacas Frescas", precio = "700", stock = 25, categoria = "Verduras", imagenResId = R.drawable.espinaca, descripcion = "Espinacas frescas y nutritivas, cosechadas el mismo día."),
            Producto(nombre = "Pimientos Tricolores", precio = "1500", stock = 20, categoria = "Verduras", imagenResId = R.drawable.pimientos, descripcion = "Mix de pimientos rojos, amarillos y verdes llenos de color y sabor."),
            
            // ORGÁNICOS
            Producto(nombre = "Miel Orgánica", precio = "5000", stock = 15, categoria = "Orgánicos", imagenResId = R.drawable.miel_organica, descripcion = "Miel pura producida por apicultores locales certificados."),
            Producto(nombre = "Quinua Orgánica", precio = "5050", stock = 35, categoria = "Orgánicos", imagenResId = R.drawable.quinua_organica, descripcion = "Superalimento rico en proteínas y fibra, libre de gluten."),
            
            // LÁCTEOS
            Producto(nombre = "Leche Entera", precio = "1500", stock = 40, categoria = "Lácteos", imagenResId = R.drawable.leche_entera, descripcion = "Leche fresca y cremosa de vacas de pastoreo libre.")
        )

        viewModelScope.launch {
            // Obtenemos los productos actuales en Firebase para comparar
            val productosEnFirebase = productoRepo.obtenerProductos().first()
            val nombresExistentes = productosEnFirebase.map { it.nombre }

            listaLocal.forEach { productoLocal ->
                if (productoLocal.nombre !in nombresExistentes) {
                    // Si NO existe en Firebase, lo creamos
                    productoRepo.guardarProducto(productoLocal)
                } else {
                    // Si YA existe, podrías optar por no hacer nada o actualizar datos
                    // Por ahora, como pediste, simplemente no se duplica.
                }
            }
        }
    }

    fun actualizarEstadoOrden(ordenId: String, nuevoEstado: String) {
        viewModelScope.launch {
            pedidoRepo.actualizarEstadoOrden(ordenId, nuevoEstado)
        }
    }

    fun eliminarUsuario(uid: String) {
        viewModelScope.launch {
            UserRepository.deleteProfile(uid).onSuccess {
                refrescarUsuarios()
            }
        }
    }

    fun refrescarUsuarios() {
        viewModelScope.launch {
            _usuarios.value = UserRepository.getAllUsers()
        }
    }

    class Factory(private val repo: PedidoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(repo) as T
    }
}
