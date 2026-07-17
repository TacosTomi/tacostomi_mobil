package com.example.restaurante

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.restaurante.data.MockData
import com.example.restaurante.databinding.ActivityOrderStatusBinding
import com.example.restaurante.model.Order
import com.example.restaurante.model.OrderStatus
import java.util.Locale

/**
 * Así va tu pedido ahorita, chiavo.
 * Simulamos que avanza con un timer, chiavo.
 * En lo real sería con internet de verdad, chiavo.
 */
class OrderStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderStatusBinding
    private val handler = Handler(Looper.getMainLooper())
    private var pedido: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedido = MockData.pedidos.find { it.id == intent.getIntExtra("pedidoId", -1) }
        val p = pedido ?: run { finish(); return }

        binding.tvMesa.text = "Mesa ${p.mesa} · Pedido #${p.id}"
        binding.tvResumen.text = p.lineas.joinToString("\n") { linea ->
            val nota = if (linea.notas.isNotBlank()) "  (${linea.notas})" else ""
            "${linea.cantidad}x ${linea.nombre}$nota"
        }
        binding.tvTotal.text = String.format(Locale.getDefault(), "Total: $%.2f", p.total)

        pintarEstado(OrderStatus.RECIBIDO)

        // Tu pedido va avanzando, chiavo
        handler.postDelayed({ avanzar(OrderStatus.EN_PREPARACION) }, 5000)
        handler.postDelayed({ avanzar(OrderStatus.LISTO) }, 10000)

        binding.btnVolver.setOnClickListener {
            // Ya que te vas, lo marcamos como listo, chiavo
            if (p.estado == OrderStatus.LISTO) p.estado = OrderStatus.COMPLETADO
            finish()
        }
    }

    private fun avanzar(estado: OrderStatus) {
        pedido?.estado = estado
        pintarEstado(estado)
    }

    private fun pintarEstado(estado: OrderStatus) {
        binding.tvEstadoActual.text = estado.etiqueta
        val pasos = listOf(binding.tvPaso1, binding.tvPaso2, binding.tvPaso3)
        val activos = when (estado) {
            OrderStatus.RECIBIDO -> 1
            OrderStatus.EN_PREPARACION -> 2
            else -> 3
        }
        pasos.forEachIndexed { i, tv -> marcarPaso(tv, i < activos) }
    }

    private fun marcarPaso(tv: TextView, activo: Boolean) {
        val color = if (activo) R.color.navy else R.color.gris_texto
        tv.setTextColor(ContextCompat.getColor(this, color))
        tv.setTypeface(null, if (activo) Typeface.BOLD else Typeface.NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
