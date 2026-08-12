package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadPerfilBinding

class ActividadPerfil : AppCompatActivity() {

    private lateinit var binding: ActividadPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupUI()
    }

    private fun setupUI() {
        pintarDatos()

        binding.btnEditarNombre.setOnClickListener { dialogoEditarNombre() }
        binding.btnCambiarPass.setOnClickListener { dialogoCambiarPassword() }

        binding.btnCerrarSesion.setOnClickListener {
            GestorSesion.cerrarSesion(this)
            val intent = Intent(this, ActividadIniciarSesion::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun pintarDatos() {
        binding.tvNombre.text = GestorSesion.nombre
        binding.tvCorreo.text = GestorSesion.correo.ifBlank { "correo@ejemplo.com" }

        val totalPedidos = GestorCarrito.pedidos.size
        val ultimaVisita = GestorCarrito.pedidos.firstOrNull()?.fecha ?: "Sin visitas aún"
        binding.tvResumen.text = "Pedidos realizados: $totalPedidos\nÚltima visita: $ultimaVisita"
    }

    private fun dialogoEditarNombre() {
        val input = EditText(this).apply {
            setText(GestorSesion.nombre)
            hint = "Nombre completo"
        }
        val contenedor = LinearLayout(this).apply {
            setPadding(48, 16, 48, 0)
            addView(input, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
        AlertDialog.Builder(this)
            .setTitle("Editar nombre")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevo = input.text.toString().trim()
                if (nuevo.isNotBlank()) {
                    GestorSesion.nombre = nuevo
                    pintarDatos()
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoCambiarPassword() {
        val etActual = EditText(this).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etNueva = EditText(this).apply {
            hint = "Nueva contraseña (mín. 6)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
            addView(etActual)
            addView(etNueva)
        }
        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                // TODO: falta un endpoint en la API (ej. PUT /usuario/password) para poder
                // cambiar la contraseña de verdad contra el backend. Por ahora solo se avisa.
                Toast.makeText(this, "Esta función aún no está conectada al servidor", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupNavigation() {
        binding.bottomNav.selectedItemId = R.id.nav_perfil
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
                R.id.nav_pedidos -> {
                    startActivity(Intent(this, ActividadHistorial::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }
}
