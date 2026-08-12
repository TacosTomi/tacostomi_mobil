package com.example.restaurante.network.dto

import com.google.gson.annotations.SerializedName

data class PedidoDto(
    val id: Int,
    @SerializedName("mesa_id") val mesaId: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("mesero_id") val meseroId: Int,
    val estado: String,
    val total: Double,
    @SerializedName("fecha_hora") val fechaHora: String
)

data class CrearPedidoRequest(
    @SerializedName("mesa_id") val mesaId: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("mesero_id") val meseroId: Int,
    val estado: String,
    val total: Double,
    @SerializedName("fecha_hora") val fechaHora: String
)
