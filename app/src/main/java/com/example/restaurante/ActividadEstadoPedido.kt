package com.example.restaurante

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.DatosPrueba
import com.example.restaurante.databinding.ActividadEstadoPedidoBinding
import com.example.restaurante.model.Pedido
import com.example.restaurante.model.OrderStatus
import java.util.Locale

class ActividadEstadoPedido : AppCompatActivity() {

    private lateinit var binding: ActividadEstadoPedidoBinding
    private val handler = Handler(Looper.getMainLooper())
    private var pedido: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadEstadoPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedido = GestorCarrito.pedidos.find { it.id == intent.getIntExtra("pedidoId", -1) }
        val p = pedido ?: run { finish(); return }

        binding.tvMesa.text = "Mesa ${p.mesa} · Pedido #${p.id}"
        binding.tvResumen.text = p.lineas.joinToString("\n") { linea ->
            val nota = if (linea.notas.isNotBlank()) "  (${linea.notas})" else ""
            "${linea.cantidad}x ${linea.nombre}$nota"
        }
        binding.tvTotal.text = String.format(Locale.getDefault(), "Total: $%.2f", p.total)

        pintarEstado(OrderStatus.RECIBIDO)

        handler.postDelayed({ avanzar(OrderStatus.EN_PREPARACION) }, 5000)
        handler.postDelayed({ avanzar(OrderStatus.LISTO) }, 10000)

        binding.btnVolver.setOnClickListener {
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
        val color = if (activo) R.color.texto_marron else R.color.texto_secundario
        tv.setTextColor(ContextCompat.getColor(this, color))
        tv.setTypeface(null, if (activo) Typeface.BOLD else Typeface.NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
