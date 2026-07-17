package com.example.restaurante

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurante.data.CartManager
import com.example.restaurante.data.MockData
import com.example.restaurante.databinding.ActivityProductDetailBinding
import java.util.Locale

// Mira los detalles de este manjar, chiavo
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var cantidad = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val producto = MockData.productoPorId(intent.getIntExtra("productoId", -1))
        if (producto == null) { finish(); return }

        binding.tvEmoji.text = producto.emoji
        binding.tvNombre.text = producto.nombre
        binding.tvPrecio.text = String.format(Locale.getDefault(), "$%.2f", producto.precio)
        binding.tvDescripcion.text = producto.descripcion
        binding.tvCantidad.text = cantidad.toString()

        // Si ya no hay, te avisamos aquí, chiavo
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
            CartManager.agregar(producto, cantidad, notas)
            Toast.makeText(this, "${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCerrar.setOnClickListener { finish() }
    }
}
