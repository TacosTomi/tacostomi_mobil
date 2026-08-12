package com.example.restaurante.network.dto

import com.google.gson.annotations.SerializedName

data class CategoriaDto(
    val id: Int,
    val nombre: String
)

data class PlatilloDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val activo: Boolean,
    @SerializedName("categoria_id") val categoriaId: Int
)
