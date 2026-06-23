package com.example.cestaOganicaIA.data.repository

import com.example.cestaOganicaIA.data.model.Producto
import com.example.cestaOganicaIA.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ProductoRepository {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())

    private fun JSONObject.toProducto(): Producto = Producto(
        idLocal = 0,
        id = optString("id", ""),
        nombre = optString("nombre", ""),
        precio = optString("precio", ""),
        stock = optInt("stock", 0),
        descripcion = optString("descripcion", ""),
        imagenResId = optInt("imagen_res_id", 0),
        imagenUri = if (isNull("imagen_uri")) null else getString("imagen_uri"),
        categoria = optString("categoria", "")
    )

    suspend fun refrescar() = withContext(Dispatchers.IO) {
        try {
            val resultados = SupabaseClient.select("productos", "order=nombre.asc")
            val lista = (0 until resultados.length()).map { i ->
                resultados.getJSONObject(i).toProducto()
            }
            _productos.value = lista
        } catch (e: Exception) {
            // Si falla la red, mantenemos la última lista conocida en memoria
        }
    }

    fun obtenerProductos(): StateFlow<List<Producto>> = _productos.asStateFlow()

    suspend fun insertarProducto(producto: Producto) {
        try {
            withContext(Dispatchers.IO) {
                val body = JSONObject().apply {
                    put("nombre", producto.nombre)
                    put("precio", producto.precio)
                    put("stock", producto.stock)
                    put("descripcion", producto.descripcion)
                    put("imagen_res_id", producto.imagenResId)
                    put("imagen_uri", producto.imagenUri)
                    put("categoria", producto.categoria)
                }
                SupabaseClient.insert("productos", body)
            }
            refrescar()
        } catch (e: Exception) {
            // Evita que un fallo de red al insertar tumbe la app
        }
    }

    suspend fun actualizarProducto(producto: Producto) {
        try {
            withContext(Dispatchers.IO) {
                val updates = JSONObject().apply {
                    put("nombre", producto.nombre)
                    put("precio", producto.precio)
                    put("stock", producto.stock)
                    put("descripcion", producto.descripcion)
                    put("imagen_res_id", producto.imagenResId)
                    put("imagen_uri", producto.imagenUri)
                    put("categoria", producto.categoria)
                }
                SupabaseClient.update("productos", "id=eq.${SupabaseClient.encode(producto.id)}", updates)
            }
            refrescar()
        } catch (e: Exception) {
            // Evita que un fallo de red al actualizar tumbe la app
        }
    }

    suspend fun eliminarProducto(producto: Producto) {
        try {
            withContext(Dispatchers.IO) {
                SupabaseClient.delete("productos", "id=eq.${SupabaseClient.encode(producto.id)}")
            }
            refrescar()
        } catch (e: Exception) {
            // Evita que un fallo de red al eliminar tumbe la app
        }
    }

    suspend fun eliminarTodo() {
        try {
            withContext(Dispatchers.IO) {
                SupabaseClient.delete("productos", "id=not.is.null")
            }
            refrescar()
        } catch (e: Exception) {
            // Evita que un fallo de red al limpiar tumbe la app
        }
    }

    suspend fun descontarStock(nombre: String, cantidad: Int) {
        try {
            withContext(Dispatchers.IO) {
                val resultados = SupabaseClient.select("productos", "nombre=eq.${SupabaseClient.encode(nombre)}")
                if (resultados.length() > 0) {
                    val producto = resultados.getJSONObject(0)
                    val stockActual = producto.optInt("stock", 0)
                    val nuevoStock = (stockActual - cantidad).coerceAtLeast(0)
                    val updates = JSONObject().apply { put("stock", nuevoStock) }
                    SupabaseClient.update("productos", "id=eq.${SupabaseClient.encode(producto.optString("id"))}", updates)
                }
            }
            refrescar()
        } catch (e: Exception) {
            // Evita que un fallo de red al descontar stock tumbe la app
        }
    }
}