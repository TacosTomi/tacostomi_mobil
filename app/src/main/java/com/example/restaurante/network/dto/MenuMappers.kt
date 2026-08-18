package com.example.restaurante.network.dto

import com.example.restaurante.model.Categoria
import com.example.restaurante.model.Producto

fun CategoriaDto.toModel(): Categoria = Categoria(
    id = id,
    nombre = nombre,
    emoji = "🍽️"
)

fun PlatilloDto.toModel(): Producto = Producto(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    categoriaId = categoriaId,
    precio = precio,
    emoji = "🌮",
    imagenUrl = imagenUrl,
    disponible = activo
)

