package com.example.cestaOganicaIA.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cestaOganicaIA.data.database.AppDatabase
import com.example.cestaOganicaIA.data.database.PedidoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val pedidoDao = AppDatabase.getDatabase(application).pedidoDao()

    val todosPedidos: StateFlow<List<PedidoEntity>> = pedidoDao.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun actualizarEstadoOrden(ordenId: Int, nuevoEstado: String) {
        viewModelScope.launch {
            pedidoDao.actualizarEstado(ordenId, nuevoEstado)
        }
    }
}
