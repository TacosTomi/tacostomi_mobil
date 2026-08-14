package com.example.restaurante.network.dto

import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.Pedido
import com.example.restaurante.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val FORMATOS_ENTRADA = listOf(
    "yyyy-MM-dd HH:mm:ss" to null,
    "yyyy-MM-dd'T'HH:mm:ss" to null,
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" to "UTC",
    "yyyy-MM-dd'T'HH:mm:ss'Z'" to "UTC"
)
private const val FORMATO_SALIDA = "dd/MM/yyyy HH:mm"

fun String.aFechaLegible(): String {
    for ((patron, zona) in FORMATOS_ENTRADA) {
        try {
            val sdf = SimpleDateFormat(patron, Locale.US)
            if (zona != null) sdf.timeZone = TimeZone.getTimeZone(zona)
            val fecha = sdf.parse(this) ?: continue
            return SimpleDateFormat(FORMATO_SALIDA, Locale.getDefault()).format(fecha)
        } catch (e: Exception) {
        }
    }
    return this
}

fun PedidoDto.aEstado(): OrderStatus =
    try {
        OrderStatus.valueOf(estado)
    } catch (e: Exception) {
        OrderStatus.RECIBIDO
    }

fun DetallePedidoDto.aModelo(): OrderLine {
    val producto = com.example.restaurante.data.CacheMenu.productoPorId(platilloId)
    return OrderLine(
        productoId = platilloId,
        nombre = producto?.nombre ?: "Producto #$platilloId",
        cantidad = cantidad,
        precioUnit = precioUnitario,
        notas = notas ?: ""
    )
}

fun PedidoDto.aModelo(): Pedido {
    val lineas = detalles?.map { it.aModelo() } ?: emptyList()
    return Pedido(
        id = id,
        fecha = fechaHora.aFechaLegible(),
        mesa = mesaId,
        lineas = lineas,
        estado = aEstado(),
        totalServidor = total
    )
}
