package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.data.CartManager
import com.example.restaurante.databinding.ItemCartBinding
import java.util.Locale

class CartAdapter(
    private val onCambio: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = CartManager.items[position]
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

        // Cambia lo que quieras antes de pedir, chiavo
        holder.b.btnMas.setOnClickListener {
            item.cantidad++
            onCambio()
        }
        holder.b.btnMenos.setOnClickListener {
            if (item.cantidad > 1) item.cantidad--
            else CartManager.items.remove(item)
            onCambio()
        }
        holder.b.btnEliminar.setOnClickListener {
            CartManager.items.remove(item)
            onCambio()
        }
    }

    override fun getItemCount() = CartManager.items.size
}
