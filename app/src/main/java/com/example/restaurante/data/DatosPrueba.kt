package com.example.restaurante.data

import com.example.restaurante.model.Categoria
import com.example.restaurante.model.Pedido
import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.OrderStatus
import com.example.restaurante.model.Producto

object DatosPrueba {

    val categorias = listOf(
        Categoria(1, "Entradas", "🥗"),
        Categoria(2, "Platos fuertes", "🍖"),
        Categoria(3, "Postres", "🍰"),
        Categoria(4, "Bebidas", "🥤")
    )

    val productos = listOf(
        Producto(1, "Guacamole con totopos", "Aguacate fresco, pico de gallo y totopos de maíz hechos en casa.", 1, 95.0, "🥑"),
        Producto(2, "Queso fundido", "Queso Chihuahua gratinado con chorizo norteño y tortillas de harina.", 1, 120.0, "🧀"),
        Producto(3, "Alitas BBQ", "8 piezas bañadas en salsa BBQ con apio y aderezo ranch.", 1, 135.0, "🍗"),
        Producto(4, "Arrachera norteña", "300 g de arrachera con frijoles charros, guacamole y tortillas.", 2, 245.0, "🥩"),
        Producto(5, "Enchiladas suizas", "Tres enchiladas de pollo bañadas en salsa verde con crema y queso.", 2, 145.0, "🌮"),
        Producto(6, "Burrito de machaca", "Burrito estilo Juárez relleno de machaca con huevo.", 2, 110.0, "🌯"),
        Producto(7, "Pescado a la plancha", "Filete de pescado con verduras salteadas y arroz blanco.", 2, 185.0, "🐟", disponible = false),
        Producto(8, "Pastel de tres leches", "Rebanada de pastel tradicional con canela y fruta de temporada.", 3, 75.0, "🍰"),
        Producto(9, "Flan napolitano", "Flan casero con caramelo, receta de la abuela.", 3, 65.0, "🍮"),
        Producto(10, "Churros con cajeta", "Orden de churros espolvoreados con azúcar y canela.", 3, 70.0, "🥨", disponible = false),
        Producto(11, "Agua fresca del día", "Pregunta por el sabor del día: horchata, jamaica o limón.", 4, 35.0, "🥤"),
        Producto(12, "Limonada mineral", "Limonada preparada al momento con agua mineral.", 4, 45.0, "🍋"),
        Producto(13, "Café de olla", "Café tradicional con piloncillo y canela.", 4, 40.0, "☕")
    )

    val mesasValidas = (1..12).toList()

    val pedidos = mutableListOf(
        Pedido(
            1002, "28/05/2026 14:35", 7,
            listOf(
                OrderLine(4, "Arrachera norteña", 1, 245.0),
                OrderLine(11, "Agua fresca del día", 2, 35.0, "Sabor jamaica")
            ),
            OrderStatus.COMPLETADO
        ),
        Pedido(
            1001, "15/05/2026 20:10", 3,
            listOf(
                OrderLine(5, "Enchiladas suizas", 2, 145.0),
                OrderLine(8, "Pastel de tres leches", 1, 75.0),
                OrderLine(13, "Café de olla", 2, 40.0)
            ),
            OrderStatus.CANCELADO
        )
    )

    private var siguienteId = 1003
    fun nuevoIdPedido(): Int = siguienteId++

    fun productoPorId(id: Int): Producto? = productos.find { it.id == id }
}
