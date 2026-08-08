package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.restaurante.data.GestorSesion
import com.example.restaurante.databinding.ActividadRegistroBinding

class ActividadRegistro : AppCompatActivity() {

    private lateinit var binding: ActividadRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActividadRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirmar = binding.etConfirmar.text.toString()

            val valido = nombre.isNotBlank() &&
                    Patterns.EMAIL_ADDRESS.matcher(correo).matches() &&
                    pass.length >= 6 && pass == confirmar

            if (valido) {
                val exito = GestorSesion.registrar(this, nombre, correo, pass)
                if (exito) {
                    startActivity(Intent(this, ActividadMenu::class.java))
                    finishAffinity()
                } else {
                    binding.tilCorreo.error = "Este correo ya está registrado"
                }
            } else {
                if (nombre.isBlank()) binding.tilNombre.error = "El nombre es obligatorio"
                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) binding.tilCorreo.error = "Escribe un correo válido"
                if (pass.length < 6) binding.tilPassword.error = "Mínimo 6 caracteres"
                if (pass != confirmar) binding.tilConfirmar.error = "Las contraseñas no coinciden"
            }
        }

        binding.tvIrLogin.setOnClickListener { finish() }
    }
}
