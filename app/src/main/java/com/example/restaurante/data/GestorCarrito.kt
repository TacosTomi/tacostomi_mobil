package com.example.restaurante.data

import android.content.Context
import android.content.SharedPreferences
import com.example.restaurante.model.ArticuloCarrito
import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.OrderStatus
import com.example.restaurante.model.Pedido
import com.example.restaurante.model.Producto
import org.json.JSONArray
import org.json.JSONObject

object GestorCarrito {
    val items = mutableListOf<ArticuloCarrito>()
    val pedidos = mutableListOf<Pedido>()

    private const val PREFS_NAME = "CarritoPrefs"
    private const val KEY_CARRITO = "carrito"
    private const val KEY_PEDIDOS = "pedidos"
    private var isInitialized = false

    fun inicializar(context: Context) {
        if (isInitialized) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        cargarCarrito(prefs)
        cargarPedidos(prefs)
        isInitialized = true
    }

    private fun cargarCarrito(prefs: SharedPreferences) {
        items.clear()
        val jsonString = prefs.getString(KEY_CARRITO, "[]")
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val prodObj = obj.getJSONObject("producto")
            val producto = Producto(
                prodObj.getInt("id"),
                prodObj.getString("nombre"),
                prodObj.getString("descripcion"),
                prodObj.getInt("categoriaId"),
                prodObj.getDouble("precio"),
                prodObj.getString("emoji"),
                prodObj.optBoolean("disponible", true)
            )
            items.add(ArticuloCarrito(producto, obj.getInt("cantidad"), obj.getString("notas")))
        }
    }

    private fun cargarPedidos(prefs: SharedPreferences) {
        pedidos.clear()
        val jsonString = prefs.getString(KEY_PEDIDOS, "[]")
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val lineas = mutableListOf<OrderLine>()
            val lineasArray = obj.getJSONArray("lineas")
            for (j in 0 until lineasArray.length()) {
                val lObj = lineasArray.getJSONObject(j)
                lineas.add(OrderLine(
                    lObj.getInt("productoId"),
                    lObj.getString("nombre"),
                    lObj.getInt("cantidad"),
                    lObj.getDouble("precioUnit"),
                    lObj.getString("notas")
                ))
            }
            val totalServidor = if (obj.has("totalServidor") && !obj.isNull("totalServidor"))
                obj.getDouble("totalServidor") else null
            pedidos.add(Pedido(
                obj.getInt("id"),
                obj.getString("fecha"),
                obj.getInt("mesa"),
                lineas,
                OrderStatus.valueOf(obj.getString("estado")),
                totalServidor
            ))
        }
    }

    fun guardarCarrito(context: Context) {
        val array = JSONArray()
        for (item in items) {
            val prodObj = JSONObject().apply {
                put("id", item.producto.id)
                put("nombre", item.producto.nombre)
                put("descripcion", item.producto.descripcion)
                put("categoriaId", item.producto.categoriaId)
                put("precio", item.producto.precio)
                put("emoji", item.producto.emoji)
                put("disponible", item.producto.disponible)
            }
            val obj = JSONObject().apply {
                put("producto", prodObj)
                put("cantidad", item.cantidad)
                put("notas", item.notas)
            }
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_CARRITO, array.toString()).apply()
    }

    fun guardarPedidos(context: Context) {
        val array = JSONArray()
        for (pedido in pedidos) {
            val lineasArray = JSONArray()
            for (linea in pedido.lineas) {
                lineasArray.put(JSONObject().apply {
                    put("productoId", linea.productoId)
                    put("nombre", linea.nombre)
                    put("cantidad", linea.cantidad)
                    put("precioUnit", linea.precioUnit)
                    put("notas", linea.notas)
                })
            }
            val obj = JSONObject().apply {
                put("id", pedido.id)
                put("fecha", pedido.fecha)
                put("mesa", pedido.mesa)
                put("estado", pedido.estado.name)
                put("lineas", lineasArray)
                if (pedido.totalServidor != null) put("totalServidor", pedido.totalServidor)
            }
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_PEDIDOS, array.toString()).apply()
    }

    fun agregar(context: Context, producto: Producto, cantidad: Int, notas: String) {
        val existente = items.find { it.producto.id == producto.id && it.notas == notas }
        if (existente != null) existente.cantidad += cantidad
        else items.add(ArticuloCarrito(producto, cantidad, notas))
        guardarCarrito(context)
    }

    fun agregarPedido(context: Context, pedido: Pedido) {
        pedidos.add(0, pedido)
        guardarPedidos(context)
    }

    /**
     * Inserta el pedido si es nuevo, o lo reemplaza si ya existe uno con el mismo id.
     * Se usa para reflejar el estado/total más reciente que llega del servidor,
     * conservando las líneas (productos) que solo existen localmente.
     */
    fun upsertPedido(context: Context, pedido: Pedido) {
        val idx = pedidos.indexOfFirst { it.id == pedido.id }
        if (idx >= 0) pedidos[idx] = pedido else pedidos.add(0, pedido)
        guardarPedidos(context)
    }

    fun total(): Double = items.sumOf { it.producto.precio * it.cantidad }

    fun limpiar(context: Context) {
        items.clear()
        guardarCarrito(context)
    }
}
