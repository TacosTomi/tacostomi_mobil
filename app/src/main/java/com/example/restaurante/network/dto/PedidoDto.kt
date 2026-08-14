package com.example.restaurante.network.dto

import com.google.gson.annotations.SerializedName

data class PedidoDto(
    val id: Int,
    @SerializedName("mesa_id") val mesaId: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("mesero_id") val meseroId: Int,
    val estado: String,
    val total: Double,
    @SerializedName("fecha_hora") val fechaHora: String,
    val detalles: List<DetallePedidoDto>? = null
)

data class DetallePedidoDto(
    @SerializedName("platillo_id") val platilloId: Int,
    val cantidad: Int,
    @SerializedName("precio_unitario") val precioUnitario: Double,
    val notas: String? = null
)

data class CrearPedidoRequest(
    @SerializedName("mesa_id") val mesaId: Int,
    @SerializedName("cliente_id") val clienteId: Int,
    @SerializedName("mesero_id") val meseroId: Int,
    val estado: String,
    val total: Double,
    @SerializedName("fecha_hora") val fechaHora: String,
    val detalles: List<DetallePedidoDto>
)
