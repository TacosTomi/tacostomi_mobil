package com.example.restaurante.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val emoji: String
)

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoriaId: Int,
    val precio: Double,
    val emoji: String,
    val disponible: Boolean = true
)

data class ArticuloCarrito(
    val producto: Producto,
    var cantidad: Int,
    var notas: String = ""
)

enum class OrderStatus(val etiqueta: String) {
    RECIBIDO("Recibido"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo para servir"),
    COMPLETADO("Completado"),
    CANCELADO("Cancelado")
}

data class OrderLine(
    val productoId: Int,
    val nombre: String,
    val cantidad: Int,
    val precioUnit: Double,
    val notas: String = ""
)

data class Pedido(
    val id: Int,
    val fecha: String,
    val mesa: Int,
    val lineas: List<OrderLine>,
    var estado: OrderStatus,
    // Total confirmado por el servidor. Si es null (pedidos viejos guardados
    // localmente antes de este cambio), se calcula a partir de las líneas.
    val totalServidor: Double? = null
) {
    val total: Double get() = totalServidor ?: lineas.sumOf { it.cantidad * it.precioUnit }
}
