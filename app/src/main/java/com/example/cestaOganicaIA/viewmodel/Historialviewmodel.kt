package com.example.cestaOganicaIA.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.PedidoEntity
import com.example.cestaOganicaIA.data.repository.PedidoRepository
import kotlinx.coroutines.flow.*

class HistorialViewModel(
    private val pedidoRepo: PedidoRepository
) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<PedidoEntity>>(emptyList())
    val pedidos: StateFlow<List<PedidoEntity>> = _pedidos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarPedidos(uid: String) {
        pedidoRepo.obtenerPorUsuario(uid)
            .onEach { _pedidos.value = it }
            .catch { e ->
                // Antes: un error aquí (sin red, permisos, etc.) tumbaba la app
                _error.value = e.message ?: "No se pudo cargar el historial"
            }
            .launchIn(viewModelScope)
    }

    fun limpiarError() { _error.value = null }

    class Factory(private val repo: PedidoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistorialViewModel(repo) as T
    }
}