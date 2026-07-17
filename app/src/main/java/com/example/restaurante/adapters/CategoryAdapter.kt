package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.R
import com.example.restaurante.databinding.ItemCategoryBinding
import com.example.restaurante.model.Category

class CategoryAdapter(
    private val categorias: List<Category>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    private var seleccionada = 0 // id de la categoría activa (0 = Todos)

    inner class VH(val b: ItemCategoryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val categoria = categorias[position]
        val contexto = holder.b.root.context
        val activa = categoria.id == seleccionada

        holder.b.tvCategoria.text = "${categoria.emoji} ${categoria.nombre}"
        holder.b.cardCategoria.setCardBackgroundColor(
            ContextCompat.getColor(contexto, if (activa) R.color.navy else R.color.blanco)
        )
        holder.b.tvCategoria.setTextColor(
            ContextCompat.getColor(contexto, if (activa) R.color.blanco else R.color.navy)
        )

        holder.b.root.setOnClickListener {
            seleccionada = categoria.id
            notifyDataSetChanged()
            onClick(categoria.id)
        }
    }

    override fun getItemCount() = categorias.size
}
