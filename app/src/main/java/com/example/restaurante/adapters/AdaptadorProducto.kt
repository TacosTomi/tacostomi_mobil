package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.databinding.ItemProductoBinding
import com.example.restaurante.model.Producto
import java.util.Locale

class AdaptadorProducto(
    private val onClick: (Producto) -> Unit
) : RecyclerView.Adapter<AdaptadorProducto.VH>() {

    private val productos = mutableListOf<Producto>()

    fun actualizar(nuevos: List<Producto>) {
        productos.clear()
        productos.addAll(nuevos)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemProductoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = productos[position]
        holder.b.tvEmoji.text = p.emoji
        holder.b.tvNombre.text = p.nombre
        holder.b.tvDescripcion.text = p.descripcion
        holder.b.tvPrecio.text = String.format(Locale.getDefault(), "$%.2f", p.precio)

        holder.b.tvAgotado.visibility = if (p.disponible) View.GONE else View.VISIBLE
        holder.b.root.alpha = if (p.disponible) 1f else 0.55f

        holder.b.root.setOnClickListener { onClick(p) }
    }

    override fun getItemCount() = productos.size
}
