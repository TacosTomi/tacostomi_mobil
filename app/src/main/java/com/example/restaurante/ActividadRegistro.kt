package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadRegistroBinding
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.RegistroRequest
import kotlinx.coroutines.launch

class ActividadRegistro : AppCompatActivity() {

    private lateinit var binding: ActividadRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupValidation()
        setupListeners()
    }

    private fun setupValidation() {
        binding.etNombre.doOnTextChanged { texto, _, _, _ ->
            binding.tilNombre.error =
                if (texto.isNullOrBlank()) "El nombre es obligatorio" else null
        }
        binding.etCorreo.doOnTextChanged { texto, _, _, _ ->
            binding.tilCorreo.error =
                if (!Patterns.EMAIL_ADDRESS.matcher(texto.toString()).matches())
                    "Escribe un correo válido" else null
        }
        binding.etPassword.doOnTextChanged { texto, _, _, _ ->
            binding.tilPassword.error =
                if ((texto?.length ?: 0) < 6) "Mínimo 6 caracteres" else null
        }
        binding.etConfirmar.doOnTextChanged { texto, _, _, _ ->
            binding.tilConfirmar.error =
                if (texto.toString() != binding.etPassword.text.toString())
                    "Las contraseñas no coinciden" else null
        }
    }

    private fun setupListeners() {
        binding.btnRegistrar.setOnClickListener {
            intentarRegistro()
        }

        binding.tvIrLogin.setOnClickListener { finish() }
    }

    private fun intentarRegistro() {
        val nombre = binding.etNombre.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val pass = binding.etPassword.text.toString()
        val confirmar = binding.etConfirmar.text.toString()

        // Validaciones locales con feedback inmediato
        var esValido = true

        if (nombre.length < 3) {
            binding.tilNombre.error = "El nombre es muy corto"
            esValido = false
        } else binding.tilNombre.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.tilCorreo.error = "Escribe un correo válido"
            esValido = false
        } else binding.tilCorreo.error = null

        if (pass.length < 6) {
            binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            esValido = false
        } else binding.tilPassword.error = null

        if (pass != confirmar) {
            binding.tilConfirmar.error = "Las contraseñas no coinciden"
            esValido = false
        } else binding.tilConfirmar.error = null

        if (!esValido) return

        setLoading(true)
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.registro(
                    RegistroRequest(nombre, correo, pass, confirmar)
                )

                if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                    val data = respuesta.body()!!.data!!
                    GestorSesion.setSesion(
                        this@ActividadRegistro,
                        data.token,
                        data.usuario.id,
                        data.usuario.nombre,
                        data.usuario.correo,
                        data.usuario.rolId
                    )
                    Toast.makeText(this@ActividadRegistro, "¡Bienvenido, $nombre!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ActividadRegistro, ActividadMenu::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Manejo de errores específicos del servidor (ej. correo ya registrado)
                    val errorBody = respuesta.errorBody()?.string() ?: ""
                    val mensaje = when {
                        errorBody.contains("correo", ignoreCase = true) -> "Este correo ya está registrado"
                        errorBody.contains("nombre", ignoreCase = true) -> "El nombre tiene un formato inválido"
                        errorBody.contains("password", ignoreCase = true) -> "La contraseña no cumple los requisitos"
                        else -> "Error en el registro: verifica tus datos"
                    }
                    Toast.makeText(this@ActividadRegistro, mensaje, Toast.LENGTH_LONG).show()
                }
            } catch (_: Exception) {
                Toast.makeText(this@ActividadRegistro, "Error de conexión: comprueba tu internet", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegistrar.isEnabled = !loading
        binding.btnRegistrar.text = if (loading) "Registrando..." else "Registrarse"
    }
}
