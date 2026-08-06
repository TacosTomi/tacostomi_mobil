package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorPedido
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.DatosPrueba
import com.example.restaurante.databinding.ActividadHistorialBinding
import com.example.restaurante.model.Pedido

class ActividadHistorial : AppCompatActivity() {

    private lateinit var binding: ActividadHistorialBinding
    private lateinit var adapter: AdaptadorPedido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val producto = DatosPrueba.productoPorId(linea.productoId)
            if (producto != null && producto.disponible) {
                GestorCarrito.agregar(this, producto, linea.cantidad, linea.notas)
                agregados++
            }
        }
        val mensaje = if (agregados == pedido.lineas.size)
            "Pedido agregado al carrito"
        else
            "Se agregaron $agregados productos (algunos ya no están disponibles)"
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun refrescar() {
        adapter.actualizar(GestorCarrito.pedidos)
        binding.tvSinPedidos.visibility =
            if (GestorCarrito.pedidos.isEmpty()) View.VISIBLE else View.GONE
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
