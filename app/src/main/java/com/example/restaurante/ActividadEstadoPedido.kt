package com.example.restaurante

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.databinding.ActividadEstadoPedidoBinding
import com.example.restaurante.model.OrderStatus
import com.example.restaurante.model.Pedido
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.aEstado
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class ActividadEstadoPedido : AppCompatActivity() {

    private lateinit var binding: ActividadEstadoPedidoBinding
    private var pedido: Pedido? = null

    companion object {
        private const val INTERVALO_SONDEO_MS = 4000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadEstadoPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GestorCarrito.inicializar(this)
        pedido = GestorCarrito.pedidos.find { it.id == intent.getIntExtra("pedidoId", -1) }
        val p = pedido ?: run { finish(); return }

        binding.tvMesa.text = "Mesa ${p.mesa} · Pedido #${p.id}"
        binding.tvResumen.text = if (p.lineas.isEmpty())
            "Detalle no disponible en este dispositivo"
        else
            p.lineas.joinToString("\n") { linea ->
                val nota = if (linea.notas.isNotBlank()) "  (${linea.notas})" else ""
                "${linea.cantidad}x ${linea.nombre}$nota"
            }
        binding.tvTotal.text = String.format(Locale.getDefault(), "Total: $%.2f", p.total)

        pintarEstado(p.estado)
        sondearEstado(p.id)

        binding.btnVolver.setOnClickListener {
            startActivity(Intent(this, ActividadMenu::class.java))
            finish()
        }
    }

    /**
     * Ya no simulamos el avance con temporizadores falsos: el estado real lo cambia
     * el backend (cocina/mesero), así que aquí solo preguntamos periódicamente.
     * lifecycleScope cancela este ciclo automáticamente si la pantalla se cierra.
     */
    private fun sondearEstado(pedidoId: Int) {
        lifecycleScope.launch {
            while (true) {
                try {
                    val respuesta = RetrofitClient.api.obtenerPedidoPorId(pedidoId)
                    if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                        val dto = respuesta.body()!!.data!!
                        val nuevoEstado = dto.aEstado()

                        pedido = pedido?.copy(estado = nuevoEstado, totalServidor = dto.total)
                        pedido?.let { GestorCarrito.upsertPedido(this@ActividadEstadoPedido, it) }
                        pintarEstado(nuevoEstado)

                        if (nuevoEstado == OrderStatus.COMPLETADO || nuevoEstado == OrderStatus.CANCELADO) {
                            break
                        }
                    }
                } catch (e: Exception) {
                    // Sin conexión momentánea: seguimos intentando en el siguiente ciclo.
                }
                delay(INTERVALO_SONDEO_MS)
            }
        }
    }

    private fun pintarEstado(estado: OrderStatus) {
        binding.tvEstadoActual.text = estado.etiqueta
        val colorEstado = if (estado == OrderStatus.CANCELADO) R.color.rojo else R.color.texto_marron
        binding.tvEstadoActual.setTextColor(ContextCompat.getColor(this, colorEstado))

        val pasos = listOf(binding.tvPaso1, binding.tvPaso2, binding.tvPaso3)
        val activos = when (estado) {
            OrderStatus.RECIBIDO -> 1
            OrderStatus.EN_PREPARACION -> 2
            OrderStatus.CANCELADO -> 0
            else -> 3
        }
        pasos.forEachIndexed { i, tv -> marcarPaso(tv, i < activos) }
    }

    private fun marcarPaso(tv: TextView, activo: Boolean) {
        val color = if (activo) R.color.texto_marron else R.color.texto_secundario
        tv.setTextColor(ContextCompat.getColor(this, color))
        tv.setTypeface(null, if (activo) Typeface.BOLD else Typeface.NORMAL)
    }
}
