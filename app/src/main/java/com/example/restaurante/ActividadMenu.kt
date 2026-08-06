package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorCategoria
import com.example.restaurante.adapters.AdaptadorProducto
import com.example.restaurante.data.DatosPrueba
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadMenuBinding
import com.example.restaurante.model.Categoria

class ActividadMenu : AppCompatActivity() {

    private lateinit var binding: ActividadMenuBinding
    private var categoriaSeleccionada = 0
    private var busqueda = ""
    private lateinit var productAdapter: AdaptadorProducto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupUI()
    }

    private fun setupUI() {
        binding.tvSaludo.text = "Hola, ${GestorSesion.nombre.split(" ").first()} 👋"

        val categorias = listOf(Categoria(0, "Todos", "🍽️")) + DatosPrueba.categorias
        binding.rvCategorias.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategorias.adapter = AdaptadorCategoria(categorias) { id ->
            categoriaSeleccionada = id
            refrescarProductos()
        }

        productAdapter = AdaptadorProducto { producto ->
            val intent = Intent(this, ActividadDetalleProducto::class.java)
            intent.putExtra("productoId", producto.id)
            startActivity(intent)
        }
        binding.rvProductos.layoutManager = GridLayoutManager(this, 2)
        binding.rvProductos.adapter = productAdapter

        binding.etBuscar.doOnTextChanged { texto, _, _, _ ->
            busqueda = texto.toString()
            refrescarProductos()
        }

        refrescarProductos()
    }

    private fun refrescarProductos() {
        val lista = DatosPrueba.productos.filter { p ->
            (categoriaSeleccionada == 0 || p.categoriaId == categoriaSeleccionada) &&
                    p.nombre.contains(busqueda, ignoreCase = true)
        }
        productAdapter.actualizar(lista)
        binding.tvSinResultados.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_menu
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> true
                R.id.nav_carrito -> {
                    startActivity(Intent(this, ActividadCarrito::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_pedidos -> {
                    startActivity(Intent(this, ActividadHistorial::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, ActividadPerfil::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
