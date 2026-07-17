package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurante.data.SessionManager
import com.example.restaurante.databinding.ActivityLoginBinding

// Entra para pedir tus tacos, chiavo
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val correo = binding.etCorreo.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            var valido = true

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                binding.tilCorreo.error = "Escribe un correo válido"
                valido = false
            } else binding.tilCorreo.error = null

            if (pass.length < 6) {
                binding.tilPassword.error = "Mínimo 6 caracteres"
                valido = false
            } else binding.tilPassword.error = null

            if (valido) {
                // Aquí conectamos con el servidor, chiavo
                SessionManager.iniciarSesion(correo)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.tvRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvRecuperar.setOnClickListener { mostrarDialogoRecuperar() }
    }

    private fun mostrarDialogoRecuperar() {
        val input = EditText(this).apply { hint = "Correo registrado" }
        val contenedor = FrameLayout(this).apply {
            setPadding(48, 16, 48, 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setMessage("Te enviaremos un enlace para restablecerla.")
            .setView(contenedor)
            .setPositiveButton("Enviar") { _, _ ->
                Toast.makeText(this, "Enlace de recuperación enviado a tu correo", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
