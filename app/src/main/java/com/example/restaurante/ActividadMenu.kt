package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorCategoria
import com.example.restaurante.adapters.AdaptadorProducto
import com.example.restaurante.data.CacheMenu
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadMenuBinding
import com.example.restaurante.model.Categoria
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.toModel
import kotlinx.coroutines.launch

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
        cargarMenu()
    }

    private fun setupUI() {
        binding.tvSaludo.text = "Hola, ${GestorSesion.nombre.split(" ").first()} 👋"

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
    }

    private fun cargarMenu() {
        binding.progressMenu.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val respCategorias = RetrofitClient.api.obtenerCategorias()
                val respPlatillos = RetrofitClient.api.obtenerPlatillos()

                if (respCategorias.isSuccessful && respPlatillos.isSuccessful &&
                    respPlatillos.body()?.exito == true
                ) {
                    val categorias = respCategorias.body()?.map { it.toModel() } ?: emptyList()
                    val productos = respPlatillos.body()?.data?.map { it.toModel() } ?: emptyList()

                    CacheMenu.categorias = categorias
                    CacheMenu.productos = productos

                    val categoriasConTodos = listOf(Categoria(0, "Todos", "🍽️")) + categorias
                    binding.rvCategorias.layoutManager =
                        LinearLayoutManager(this@ActividadMenu, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvCategorias.adapter = AdaptadorCategoria(categoriasConTodos) { id ->
                        categoriaSeleccionada = id
                        refrescarProductos()
                    }

                    refrescarProductos()
                } else {
                    Toast.makeText(this@ActividadMenu, "No se pudo cargar el menú", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ActividadMenu, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressMenu.visibility = View.GONE
            }
        }
    }

    private fun refrescarProductos() {
        val lista = CacheMenu.productos.filter { p ->
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
