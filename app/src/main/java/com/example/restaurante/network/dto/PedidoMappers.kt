package com.example.restaurante.network.dto

import com.example.restaurante.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Laravel puede serializar la fecha en más de un formato según cómo esté
// casteado el atributo en el modelo (string plano vs datetime ISO). Probamos
// varios patrones en vez de asumir uno solo, para no tronar si cambia.
private val FORMATOS_ENTRADA = listOf(
    "yyyy-MM-dd HH:mm:ss" to null,
    "yyyy-MM-dd'T'HH:mm:ss" to null,
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" to "UTC",
    "yyyy-MM-dd'T'HH:mm:ss'Z'" to "UTC"
)
private const val FORMATO_SALIDA = "dd/MM/yyyy HH:mm"

/** Convierte la fecha que manda el backend a "dd/MM/yyyy HH:mm" para mostrar en la app. */
fun String.aFechaLegible(): String {
    for ((patron, zona) in FORMATOS_ENTRADA) {
        try {
            val sdf = SimpleDateFormat(patron, Locale.US)
            if (zona != null) sdf.timeZone = TimeZone.getTimeZone(zona)
            val fecha = sdf.parse(this) ?: continue
            return SimpleDateFormat(FORMATO_SALIDA, Locale.getDefault()).format(fecha)
        } catch (e: Exception) {
            // probamos el siguiente formato
        }
    }
    return this // si ningún formato coincide, mostramos el texto tal cual en vez de tronar
}

/** El backend valida que `estado` sea uno de los OrderStatus, así que el mapeo es directo. */
fun PedidoDto.aEstado(): OrderStatus =
    try {
        OrderStatus.valueOf(estado)
    } catch (e: Exception) {
        OrderStatus.RECIBIDO
    }
