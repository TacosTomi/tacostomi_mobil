package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.R
import com.example.restaurante.databinding.ItemOrderBinding
import com.example.restaurante.model.Order
import com.example.restaurante.model.OrderStatus
import java.util.Locale

class OrderAdapter(
    private val onClick: (Order) -> Unit,
    private val onRepetir: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.VH>() {

    private val pedidos = mutableListOf<Order>()

    fun actualizar(nuevos: List<Order>) {
        pedidos.clear()
        pedidos.addAll(nuevos)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemOrderBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pedido = pedidos[position]
        val contexto = holder.b.root.context
        val totalProductos = pedido.lineas.sumOf { it.cantidad }

        holder.b.tvFecha.text = pedido.fecha
        holder.b.tvDetalle.text = "Mesa ${pedido.mesa} · $totalProductos productos"
        holder.b.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", pedido.total)
        holder.b.tvEstado.text = pedido.estado.etiqueta

        val colorEstado = when (pedido.estado) {
            OrderStatus.CANCELADO -> R.color.rojo
            OrderStatus.COMPLETADO -> R.color.verde
            else -> R.color.periwinkle
        }
        holder.b.tvEstado.setTextColor(ContextCompat.getColor(contexto, colorEstado))

        holder.b.root.setOnClickListener { onClick(pedido) }
        holder.b.btnRepetir.setOnClickListener { onRepetir(pedido) }
    }

    override fun getItemCount() = pedidos.size
}
