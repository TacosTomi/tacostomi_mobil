package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.restaurante.data.GestorCarrito
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadPerfilBinding
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.CambioPasswordRequest
import kotlinx.coroutines.launch

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

    private fun dialogoCambiarPassword() {
        val etActual = EditText(this).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etNueva = EditText(this).apply {
            hint = "Nueva contraseña (mín. 6)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etConfirmar = EditText(this).apply {
            hint = "Confirmar nueva contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 16)
            addView(etActual)
            addView(etNueva)
            addView(etConfirmar)
        }
        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                val actual = etActual.text.toString()
                val nueva = etNueva.text.toString()
                val confirmar = etConfirmar.text.toString()

                if (actual.isBlank() || nueva.length < 6 || nueva != confirmar) {
                    Toast.makeText(this, "Verifica los datos ingresados", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                ejecutarCambioPassword(actual, nueva, confirmar)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ejecutarCambioPassword(actual: String, nueva: String, confirmar: String) {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.cambiarPassword(
                    CambioPasswordRequest(actual, nueva, confirmar)
                )

                if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                    Toast.makeText(this@ActividadPerfil, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    val msg = respuesta.body()?.mensaje ?: "No se pudo cambiar la contraseña"
                    Toast.makeText(this@ActividadPerfil, msg, Toast.LENGTH_LONG).show()
                }
            } catch (_: Exception) {
                Toast.makeText(this@ActividadPerfil, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
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
