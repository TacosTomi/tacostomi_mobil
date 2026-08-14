package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorPedido
import com.example.restaurante.data.CacheMenu
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadHistorialBinding
import com.example.restaurante.model.Pedido
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.aModelo
import kotlinx.coroutines.launch

class ActividadHistorial : AppCompatActivity() {

    private lateinit var binding: ActividadHistorialBinding
    private lateinit var adapter: AdaptadorPedido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GestorCarrito.inicializar(this)
        setupNavigation()
        setupUI()
    }

    private fun setupUI() {
        adapter = AdaptadorPedido(
            onClick = { pedido ->
                val intent = Intent(this, ActividadDetallePedido::class.java)
                intent.putExtra("pedidoId", pedido.id)
                startActivity(intent)
            },
            onRepetir = { pedido -> repetirPedido(pedido) }
        )
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        binding.rvPedidos.adapter = adapter
        refrescar()
    }

    private fun repetirPedido(pedido: Pedido) {
        var agregados = 0
        pedido.lineas.forEach { linea ->
            val producto = CacheMenu.productoPorId(linea.productoId)
            if (producto != null && producto.disponible) {
                GestorCarrito.agregar(this, producto, linea.cantidad, linea.notas)
                agregados++
            }
        }
        val mensaje = when {
            pedido.lineas.isEmpty() -> "No se encontró el detalle de este pedido en este dispositivo"
            agregados == pedido.lineas.size -> "Pedido agregado al carrito"
            else -> "Se agregaron $agregados productos (algunos ya no están disponibles)"
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun refrescar() {
        binding.progressHistorial.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.obtenerPedidosPorCliente(GestorSesion.id)
                if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                    // El backend regresa los pedidos del más antiguo al más reciente
                    // (orderBy fecha_hora), así que invertimos el orden para mostrarlos aquí.
                    val pedidosServidor = respuesta.body()?.data.orEmpty().asReversed()

                    val pedidos = pedidosServidor.map { dto ->
                        val modeloServer = dto.aModelo()
                        if (modeloServer.lineas.isNotEmpty()) {
                            modeloServer
                        } else {
                            val local = GestorCarrito.pedidos.find { it.id == dto.id }
                            modeloServer.copy(lineas = local?.lineas ?: emptyList())
                        }
                    }

                    pedidos.forEach { GestorCarrito.upsertPedido(this@ActividadHistorial, it) }
                    mostrarPedidos(pedidos)
                } else {
                    val mensaje = respuesta.body()?.mensaje ?: "No se pudieron cargar tus pedidos"
                    Toast.makeText(this@ActividadHistorial, mensaje, Toast.LENGTH_LONG).show()
                    mostrarPedidos(GestorCarrito.pedidos)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ActividadHistorial, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                mostrarPedidos(GestorCarrito.pedidos)
            } finally {
                binding.progressHistorial.visibility = View.GONE
            }
        }
    }

    private fun mostrarPedidos(pedidos: List<Pedido>) {
        adapter.actualizar(pedidos)
        binding.tvSinPedidos.visibility = if (pedidos.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_pedidos
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> {
                    startActivity(Intent(this, ActividadMenu::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_carrito -> {
                    startActivity(Intent(this, ActividadCarrito::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_pedidos -> true
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

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) refrescar()
    }
}
