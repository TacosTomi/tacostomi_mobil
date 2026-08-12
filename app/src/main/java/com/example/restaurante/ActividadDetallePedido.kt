package com.example.restaurante

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.restaurante.data.CacheMenu
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.databinding.ActividadDetallePedidoBinding
import com.example.restaurante.model.OrderStatus
import java.util.Locale

class ActividadDetallePedido : AppCompatActivity() {

    private lateinit var binding: ActividadDetallePedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadDetallePedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pedido = GestorCarrito.pedidos.find { it.id == intent.getIntExtra("pedidoId", -1) }
        if (pedido == null) { finish(); return }

        binding.tvTitulo.text = "Pedido #${pedido.id}"
        binding.tvFecha.text = "${pedido.fecha} · Mesa ${pedido.mesa}"
        binding.tvEstado.text = pedido.estado.etiqueta
        binding.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", pedido.total)

        val colorEstado = when (pedido.estado) {
            OrderStatus.CANCELADO -> R.color.rojo
            OrderStatus.COMPLETADO -> R.color.verde
            else -> R.color.terracota
        }
        binding.tvEstado.setTextColor(ContextCompat.getColor(this, colorEstado))

        if (pedido.lineas.isEmpty()) {
            val tv = TextView(this).apply {
                text = "El detalle de los platillos no está disponible en este dispositivo."
                textSize = 15f
                setTextColor(ContextCompat.getColor(this@ActividadDetallePedido, R.color.texto_secundario))
            }
            binding.contenedorLineas.addView(tv)
            binding.btnRepetir.isEnabled = false
        } else {
            pedido.lineas.forEach { linea ->
                val tv = TextView(this).apply {
                    val nota =
                        if (linea.notas.isNotBlank()) "\n   Nota: ${linea.notas}"
                        else ""
                    text = String.format(Locale.getDefault(),
                        "%dx %s — $%.2f%s", linea.cantidad, linea.nombre,
                        linea.cantidad * linea.precioUnit, nota)
                    textSize = 15f
                    setTextColor(ContextCompat.getColor(this@ActividadDetallePedido, R.color.texto_marron))
                    setPadding(0, 12, 0, 12)
                }
                binding.contenedorLineas.addView(tv)
            }
        }

        binding.btnRepetir.setOnClickListener {
            var agregados = 0
            pedido.lineas.forEach { linea ->
                val producto = CacheMenu.productoPorId(linea.productoId)
                if (producto != null && producto.disponible) {
                    GestorCarrito.agregar(this, producto, linea.cantidad, linea.notas)
                    agregados++
                }
            }
            Toast.makeText(this, "Se agregaron $agregados productos al carrito", Toast.LENGTH_LONG).show()
            finish()
        }

        binding.btnCerrar.setOnClickListener { finish() }
    }
}
