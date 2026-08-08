package com.example.restaurante.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurante.R
import com.example.restaurante.databinding.ItemCategoriaBinding
import com.example.restaurante.model.Categoria

class AdaptadorCategoria(
    private val categorias: List<Categoria>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<AdaptadorCategoria.VH>() {

    private var seleccionada = 0 

    inner class VH(val b: ItemCategoriaBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val categoria = categorias[position]
        val contexto = holder.b.root.context
        val activa = categoria.id == seleccionada

        holder.b.tvCategoria.text = "${categoria.emoji} ${categoria.nombre}"
        holder.b.cardCategoria.setCardBackgroundColor(
            ContextCompat.getColor(contexto, if (activa) R.color.texto_marron else R.color.blanco)
        )
        holder.b.tvCategoria.setTextColor(
            ContextCompat.getColor(contexto, if (activa) R.color.blanco else R.color.texto_marron)
        )

        holder.b.root.setOnClickListener {
            seleccionada = categoria.id
            notifyDataSetChanged()
            onClick(categoria.id)
        }
    }

    override fun getItemCount() = categorias.size
}
