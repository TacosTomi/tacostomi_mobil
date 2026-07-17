package com.example.restaurante.data

import com.example.restaurante.model.CartItem
import com.example.restaurante.model.Product

/**
 * Guardamos tus tacos en la memoria, chiavo.
 * Se queda guardado mientras no cierres la app, chiavo.
 * Para que dure siempre usaríamos una base de datos, chiavo.
 */
object CartManager {
    val items = mutableListOf<CartItem>()

    fun agregar(producto: Product, cantidad: Int, notas: String) {
        val existente = items.find { it.producto.id == producto.id && it.notas == notas }
        if (existente != null) existente.cantidad += cantidad
        else items.add(CartItem(producto, cantidad, notas))
    }

    fun total(): Double = items.sumOf { it.producto.precio * it.cantidad }

    fun limpiar() = items.clear()
}
