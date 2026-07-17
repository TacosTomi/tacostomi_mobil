package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurante.adapters.OrderAdapter
import com.example.restaurante.data.CartManager
import com.example.restaurante.data.MockData
import com.example.restaurante.databinding.FragmentHistoryBinding
import com.example.restaurante.model.Order

// Tus pedidos pasados están aquí, chiavo
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderAdapter(
            onClick = { pedido ->
                val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                intent.putExtra("pedidoId", pedido.id)
                startActivity(intent)
            },
            onRepetir = { pedido -> repetirPedido(pedido) }
        )
        binding.rvPedidos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPedidos.adapter = adapter
        refrescar()
    }

    // ¿Quieres lo mismo? Lo agregamos al carrito, chiavo
    private fun repetirPedido(pedido: Order) {
        var agregados = 0
        pedido.lineas.forEach { linea ->
            val producto = MockData.productoPorId(linea.productoId)
            if (producto != null && producto.disponible) {
                CartManager.agregar(producto, linea.cantidad, linea.notas)
                agregados++
            }
        }
        val mensaje = if (agregados == pedido.lineas.size)
            "Pedido agregado al carrito"
        else
            "Se agregaron $agregados productos (algunos ya no están disponibles)"
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_LONG).show()
    }

    private fun refrescar() {
        // Los más nuevos van primero, chiavo
        adapter.actualizar(MockData.pedidos)
        binding.tvSinPedidos.visibility =
            if (MockData.pedidos.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) refrescar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
