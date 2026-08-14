package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.AdaptadorCarrito
import com.example.restaurante.data.DatosPrueba
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadCarritoBinding
import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.OrderStatus
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.CrearPedidoRequest
import com.example.restaurante.network.dto.DetallePedidoDto
import com.example.restaurante.network.dto.aModelo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActividadCarrito : AppCompatActivity() {

    private lateinit var binding: ActividadCarritoBinding
    private lateinit var adapter: AdaptadorCarrito

    companion object {
        // TODO: ajustar al id real de un mesero que exista en tu tabla `meseros`.
        // La app todavía no tiene flujo para que el cliente elija mesero (el pedido
        // se hace directo desde su celular), así que se asigna este por defecto.
        private const val MESERO_ID_POR_DEFECTO = 1
    }

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
        val vista = layoutInflater.inflate(R.layout.dialogo_seleccionar_mesa, null)
        val grid = vista.findViewById<GridLayout>(R.id.gridMesas)

        val dialogo = AlertDialog.Builder(this)
            .setView(vista)
            .setNegativeButton("Cancelar", null)
            .create()

        DatosPrueba.mesasValidas.forEach { numeroMesa ->
            val boton = Button(this).apply {
                text = numeroMesa.toString()
                setBackgroundResource(R.drawable.bg_mesa)
                setTextColor(getColor(R.color.texto_marron))
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
                layoutParams = params
                setPadding(0, 32, 0, 32)
                setOnClickListener {
                    dialogo.dismiss()
                    enviarPedido(numeroMesa)
                }
            }
            grid.addView(boton)
        }

        dialogo.show()
    }

    private fun enviarPedido(mesa: Int) {
        val lineas = GestorCarrito.items.map {
            OrderLine(it.producto.id, it.producto.nombre, it.cantidad, it.producto.precio, it.notas)
        }
        
        val detallesDto = GestorCarrito.items.map {
            DetallePedidoDto(
                platilloId = it.producto.id,
                cantidad = it.cantidad,
                precioUnitario = it.producto.precio,
                notas = it.notas
            )
        }

        val total = GestorCarrito.total()
        val fechaHoraServidor = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

        binding.btnConfirmar.isEnabled = false
        binding.btnConfirmar.text = "Enviando..."

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.crearPedido(
                    CrearPedidoRequest(
                        mesaId = mesa,
                        clienteId = GestorSesion.id,
                        meseroId = MESERO_ID_POR_DEFECTO,
                        estado = OrderStatus.RECIBIDO.name,
                        total = total,
                        fechaHora = fechaHoraServidor,
                        detalles = detallesDto
                    )
                )

                if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                    val dto = respuesta.body()!!.data!!
                    val pedido = dto.aModelo().copy(lineas = lineas)
                    GestorCarrito.upsertPedido(this@ActividadCarrito, pedido)
                    GestorCarrito.limpiar(this@ActividadCarrito)
                    actualizarResumen()

                    val intent = Intent(this@ActividadCarrito, ActividadEstadoPedido::class.java)
                    intent.putExtra("pedidoId", pedido.id)
                    startActivity(intent)
                    finish()
                } else {
                    val mensaje = respuesta.body()?.mensaje ?: "No se pudo enviar el pedido"
                    Toast.makeText(this@ActividadCarrito, mensaje, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ActividadCarrito, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnConfirmar.isEnabled = true
                binding.btnConfirmar.text = "Confirmar y enviar pedido"
            }
        }
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
