package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.restaurante.data.SessionManager
import com.example.restaurante.databinding.ActivityRegisterBinding

// Únete a la familia de Tacos Tommy, chiavo
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Checamos que todo esté bien mientras escribes, chiavo
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
                SessionManager.registrar(nombre, correo, pass)
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
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
