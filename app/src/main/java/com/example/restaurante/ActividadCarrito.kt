package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorCarrito
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.DatosPrueba
import com.example.restaurante.databinding.ActividadCarritoBinding
import com.example.restaurante.model.Pedido
import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActividadCarrito : AppCompatActivity() {

    private lateinit var binding: ActividadCarritoBinding
    private lateinit var adapter: AdaptadorCarrito

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupUI()
    }

    private fun setupUI() {
        adapter = AdaptadorCarrito { actualizarResumen() }
        binding.rvCarrito.layoutManager = LinearLayoutManager(this)
        binding.rvCarrito.adapter = adapter

        binding.btnConfirmar.setOnClickListener { pedirNumeroDeMesa() }
        actualizarResumen()
    }

    private fun actualizarResumen() {
        adapter.notifyDataSetChanged()
        val vacio = GestorCarrito.items.isEmpty()
        binding.tvVacio.visibility = if (vacio) View.VISIBLE else View.GONE
        binding.rvCarrito.visibility = if (vacio) View.GONE else View.VISIBLE
        binding.btnConfirmar.isEnabled = !vacio
        binding.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", GestorCarrito.total())
    }

    private fun pedirNumeroDeMesa() {
        val input = EditText(this).apply {
            hint = "Número de mesa"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val contenedor = FrameLayout(this).apply {
            setPadding(48, 16, 48, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Confirmar pedido")
            .setMessage("Escribe el número de tu mesa")
            .setView(contenedor)
            .setPositiveButton("Enviar pedido") { _, _ ->
                val mesa = input.text.toString().toIntOrNull()
                if (mesa == null || mesa !in DatosPrueba.mesasValidas) {
                    Toast.makeText(this,
                        "Mesa inválida. Mesas registradas: 1 a ${DatosPrueba.mesasValidas.last()}",
                        Toast.LENGTH_LONG).show()
                } else {
                    enviarPedido(mesa)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarPedido(mesa: Int) {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val lineas = GestorCarrito.items.map {
            OrderLine(it.producto.id, it.producto.nombre, it.cantidad, it.producto.precio, it.notas)
        }
        val pedido = Pedido(DatosPrueba.nuevoIdPedido(), fecha, mesa, lineas, OrderStatus.RECIBIDO)
        GestorCarrito.agregarPedido(this, pedido)
        GestorCarrito.limpiar(this)
        actualizarResumen()

        val intent = Intent(this, ActividadEstadoPedido::class.java)
        intent.putExtra("pedidoId", pedido.id)
        startActivity(intent)
    }

    private fun setupNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_carrito
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu -> {
                    startActivity(Intent(this, ActividadMenu::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_carrito -> true
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

    override fun onResume() {
        super.onResume()
        actualizarResumen()
    }
}
