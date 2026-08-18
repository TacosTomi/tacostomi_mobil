package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.databinding.ItemCarritoBinding
import com.example.restaurante.model.ArticuloCarrito
import java.util.Locale

class AdaptadorCarrito(
    private val onCambio: () -> Unit
) : RecyclerView.Adapter<AdaptadorCarrito.VH>() {

    companion object {
        private const val CANTIDAD_MAXIMA = 20
    }

    inner class VH(val b: ItemCarritoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = GestorCarrito.items[position]
        holder.b.tvEmoji.text = item.producto.emoji
        holder.b.tvNombre.text = item.producto.nombre
        holder.b.tvCantidad.text = item.cantidad.toString()
        holder.b.tvSubtotal.text = String.format(
            Locale.getDefault(), "$%.2f", item.producto.precio * item.cantidad
        )

        if (item.notas.isNotBlank()) {
            holder.b.tvNotas.visibility = View.VISIBLE
            holder.b.tvNotas.text = "Nota: ${item.notas}"
        } else {
            holder.b.tvNotas.visibility = View.GONE
        }

        holder.b.btnMas.setOnClickListener {
            if (item.cantidad < CANTIDAD_MAXIMA) {
                item.cantidad++
                onCambio()
            }
        }
        holder.b.btnMenos.setOnClickListener {
            if (item.cantidad > 1) item.cantidad--
            else GestorCarrito.items.remove(item)
            onCambio()
        }
        holder.b.btnEliminar.setOnClickListener {
            GestorCarrito.items.remove(item)
            onCambio()
        }
    }

    override fun getItemCount() = GestorCarrito.items.size
}
