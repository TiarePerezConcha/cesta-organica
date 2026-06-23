package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.model.Resena
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import com.example.cestaOganicaIA.data.repository.ResenaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ResenaViewModel(
    private val resenaRepo: ResenaRepository,
    private val pedidoRepo: PedidoRepository
) : ViewModel() {

    private val _resenas = MutableStateFlow<List<Resena>>(emptyList())
    val resenas: StateFlow<List<Resena>> = _resenas.asStateFlow()

    private val _puedeResenar = MutableStateFlow(false)
    val puedeResenar: StateFlow<Boolean> = _puedeResenar.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun limpiarError() { _error.value = null }

    /** Carga las reseñas de un producto en tiempo real. */
    fun cargarResenas(nombreProducto: String) {
        viewModelScope.launch {
            resenaRepo.obtenerResenasPorProducto(nombreProducto)
                .catch { e -> _error.value = "No se pudieron cargar las reseñas: ${e.message}" }
                .collect { _resenas.value = it }
        }
    }

    /**
     * Verifica si el usuario (uid) tiene al menos un pedido confirmado
     * que incluya el producto indicado. Si no hay compra, no puede reseñar.
     */
    fun verificarCompra(uid: String, nombreProducto: String) {
        if (uid.isEmpty() || uid == "INVITADO") {
            _puedeResenar.value = false
            return
        }
        viewModelScope.launch {
            pedidoRepo.obtenerPorUsuario(uid)
                .catch { e ->
                    _error.value = "No se pudo verificar tus compras: ${e.message}"
                    _puedeResenar.value = false
                }
                .collect { pedidos: List<PedidoEntity> ->
                    _puedeResenar.value = pedidos.any {
                        it.nombreProducto.equals(nombreProducto, ignoreCase = true)
                    }
                }
        }
    }

    /** Agrega una reseña nueva, solo si puedeResenar es true. */
    fun agregarResena(
        nombreProducto: String,
        idUsuario: String,
        nombreUsuario: String,
        calificacion: Int,
        comentario: String
    ) {
        if (!_puedeResenar.value) {
            _error.value = "Usted no tiene pedidos anteriores de este producto"
            return
        }
        if (calificacion !in 1..5) {
            _error.value = "Selecciona una calificación de 1 a 5 estrellas"
            return
        }
        if (comentario.isBlank()) {
            _error.value = "Escribe un comentario para tu reseña"
            return
        }
        viewModelScope.launch {
            try {
                resenaRepo.agregarResena(nombreProducto, idUsuario, nombreUsuario, calificacion, comentario)
            } catch (e: Exception) {
                _error.value = "No se pudo guardar la reseña: ${e.message}"
            }
        }
    }

    class Factory(
        private val resenaRepo: ResenaRepository,
        private val pedidoRepo: PedidoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ResenaViewModel(resenaRepo, pedidoRepo) as T
    }
}