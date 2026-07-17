package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.CategoryAdapter
import com.example.restaurante.adapters.ProductAdapter
import com.example.restaurante.data.MockData
import com.example.restaurante.data.SessionManager
import com.example.restaurante.databinding.FragmentMenuBinding
import com.example.restaurante.model.Category

// Mira todo lo que tenemos para ti, chiavo
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var categoriaSeleccionada = 0 // 0 = Todos
    private var busqueda = ""
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSaludo.text = "Hola, ${SessionManager.nombre.split(" ").first()} 👋"

        val categorias = listOf(Category(0, "Todos", "🍽️")) + MockData.categorias
        binding.rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategorias.adapter = CategoryAdapter(categorias) { id ->
            categoriaSeleccionada = id
            refrescarProductos()
        }

        productAdapter = ProductAdapter { producto ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("productoId", producto.id)
            startActivity(intent)
        }
        binding.rvProductos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProductos.adapter = productAdapter

        binding.etBuscar.doOnTextChanged { texto, _, _, _ ->
            busqueda = texto.toString()
            refrescarProductos()
        }

        refrescarProductos()
    }

    private fun refrescarProductos() {
        val lista = MockData.productos.filter { p ->
            (categoriaSeleccionada == 0 || p.categoriaId == categoriaSeleccionada) &&
                    p.nombre.contains(busqueda, ignoreCase = true)
        }
        productAdapter.actualizar(lista)
        binding.tvSinResultados.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
