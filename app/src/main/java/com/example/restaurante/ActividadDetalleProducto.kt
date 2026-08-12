package com.example.restaurante

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.CacheMenu
import com.example.restaurante.databinding.ActividadDetalleProductoBinding
import java.util.Locale


class ActividadDetalleProducto : AppCompatActivity() {

    private lateinit var binding: ActividadDetalleProductoBinding
    private var cantidad = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadDetalleProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = CacheMenu.productoPorId(intent.getIntExtra("productoId", -1))
        if (producto == null) { finish(); return }

        binding.tvEmoji.text = producto.emoji
        binding.tvNombre.text = producto.nombre
        binding.tvPrecio.text = String.format(Locale.getDefault(), "$%.2f", producto.precio)
        binding.tvDescripcion.text = producto.descripcion
        binding.tvCantidad.text = cantidad.toString()

        if (!producto.disponible) {
            binding.tvAgotado.visibility = View.VISIBLE
            binding.grupoCantidad.visibility = View.GONE
            binding.tilNotas.visibility = View.GONE
            binding.btnAgregar.visibility = View.GONE
        }

        binding.btnMenos.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                binding.tvCantidad.text = cantidad.toString()
            }
        }
        binding.btnMas.setOnClickListener {
            cantidad++
            binding.tvCantidad.text = cantidad.toString()
        }

        binding.btnAgregar.setOnClickListener {
            val notas = binding.etNotas.text.toString().trim()
            GestorCarrito.agregar(this, producto, cantidad, notas)
            Toast.makeText(this, "${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCerrar.setOnClickListener { finish() }
    }
}
