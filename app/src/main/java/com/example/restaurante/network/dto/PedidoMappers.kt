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

fun PedidoDto.aEstado(): OrderStatus = estado.aOrderStatus()

/**
 * El enum `estado_pedido` en Postgres usa valores distintos a los nombres
 * del enum de Kotlin: minúsculas, con acento y espacio en "en preparación",
 * y sin un valor para "cancelado". Estas funciones traducen entre ambos.
 */
fun OrderStatus.aValorBackend(): String = when (this) {
    OrderStatus.RECIBIDO -> "recibido"
    OrderStatus.EN_PREPARACION -> "en preparación"
    OrderStatus.LISTO -> "listo"
    OrderStatus.COMPLETADO -> "entregado"
    // El enum de Postgres no tiene un valor para "cancelado" todavía.
    // Se manda "recibido" como fallback para no romper la petición; hay que
    // decidir con el equipo si se agrega "cancelado" al enum del backend.
    OrderStatus.CANCELADO -> "recibido"
}

fun String.aOrderStatus(): OrderStatus = when (this.lowercase()) {
    "recibido" -> OrderStatus.RECIBIDO
    "en preparación", "en preparacion" -> OrderStatus.EN_PREPARACION
    "listo" -> OrderStatus.LISTO
    "entregado" -> OrderStatus.COMPLETADO
    else -> OrderStatus.RECIBIDO
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
