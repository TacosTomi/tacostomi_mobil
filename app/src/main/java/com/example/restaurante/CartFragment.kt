package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.CartAdapter
import com.example.restaurante.data.CartManager
import com.example.restaurante.data.MockData
import com.example.restaurante.databinding.FragmentCartBinding
import com.example.restaurante.model.Order
import com.example.restaurante.model.OrderLine
import com.example.restaurante.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Aquí manejamos el carrito y los pedidos, chiavo
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter { actualizarResumen() }
        binding.rvCarrito.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCarrito.adapter = adapter

        binding.btnConfirmar.setOnClickListener { pedirNumeroDeMesa() }
        actualizarResumen()
    }

    private fun actualizarResumen() {
        adapter.notifyDataSetChanged()
        val vacio = CartManager.items.isEmpty()
        binding.tvVacio.visibility = if (vacio) View.VISIBLE else View.GONE
        binding.rvCarrito.visibility = if (vacio) View.GONE else View.VISIBLE
        binding.btnConfirmar.isEnabled = !vacio
        binding.tvTotal.text = String.format(Locale.getDefault(), "$%.2f", CartManager.total())
    }

    // Pedimos la mesa para saber a dónde llevar los tacos, chiavo
    private fun pedirNumeroDeMesa() {
        val input = EditText(requireContext()).apply {
            hint = "Número de mesa"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        val contenedor = FrameLayout(requireContext()).apply {
            setPadding(48, 16, 48, 0)
            addView(input)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar pedido")
            .setMessage("Escribe el número de tu mesa")
            .setView(contenedor)
            .setPositiveButton("Enviar pedido") { _, _ ->
                val mesa = input.text.toString().toIntOrNull()
                if (mesa == null || mesa !in MockData.mesasValidas) {
                    Toast.makeText(requireContext(),
                        "Mesa inválida. Mesas registradas: 1 a ${MockData.mesasValidas.last()}",
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
        val lineas = CartManager.items.map {
            OrderLine(it.producto.id, it.producto.nombre, it.cantidad, it.producto.precio, it.notas)
        }
        val pedido = Order(MockData.nuevoIdPedido(), fecha, mesa, lineas, OrderStatus.RECIBIDO)
        MockData.pedidos.add(0, pedido)
        CartManager.limpiar()
        actualizarResumen()

        val intent = Intent(requireContext(), OrderStatusActivity::class.java)
        intent.putExtra("pedidoId", pedido.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        actualizarResumen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
