package com.example.restaurante.data

import com.example.restaurante.model.Categoria
import com.example.restaurante.model.Producto

object CacheMenu {
    var categorias: List<Categoria> = emptyList()
    var productos: List<Producto> = emptyList()

    fun productoPorId(id: Int): Producto? = productos.find { it.id == id }

}
