package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.databinding.ItemProductBinding
import com.example.restaurante.model.Product
import java.util.Locale

class ProductAdapter(
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    private val productos = mutableListOf<Product>()

    fun actualizar(nuevos: List<Product>) {
        productos.clear()
        productos.addAll(nuevos)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = productos[position]
        holder.b.tvEmoji.text = p.emoji
        holder.b.tvNombre.text = p.nombre
        holder.b.tvDescripcion.text = p.descripcion
        holder.b.tvPrecio.text = String.format(Locale.getDefault(), "$%.2f", p.precio)

        // Si dice agotado, ya se lo acabaron, chiavo
        holder.b.tvAgotado.visibility = if (p.disponible) View.GONE else View.VISIBLE
        holder.b.root.alpha = if (p.disponible) 1f else 0.55f

        holder.b.root.setOnClickListener { onClick(p) }
    }

    override fun getItemCount() = productos.size
}
