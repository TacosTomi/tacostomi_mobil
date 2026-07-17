package com.example.restaurante

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.restaurante.data.MockData
import com.example.restaurante.data.SessionManager
import com.example.restaurante.databinding.FragmentProfileBinding

// Este eres tú en Tacos Tommy, chiavo
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pintarDatos()

        binding.btnEditarNombre.setOnClickListener { dialogoEditarNombre() }
        binding.btnCambiarPass.setOnClickListener { dialogoCambiarPassword() }

        binding.btnCerrarSesion.setOnClickListener {
            // Tu carrito se queda ahí aunque salgas, chiavo
            SessionManager.cerrarSesion()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun pintarDatos() {
        binding.tvNombre.text = SessionManager.nombre
        binding.tvCorreo.text = SessionManager.correo.ifBlank { "correo@ejemplo.com" }

        // Qué tanto has venido a vernos, chiavo
        val totalPedidos = MockData.pedidos.size
        val ultimaVisita = MockData.pedidos.firstOrNull()?.fecha ?: "Sin visitas aún"
        binding.tvResumen.text = "Pedidos realizados: $totalPedidos\nÚltima visita: $ultimaVisita"
    }

    private fun dialogoEditarNombre() {
        val input = EditText(requireContext()).apply {
            setText(SessionManager.nombre)
            hint = "Nombre completo"
        }
        val contenedor = LinearLayout(requireContext()).apply {
            setPadding(48, 16, 48, 0)
            addView(input, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Editar nombre")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevo = input.text.toString().trim()
                if (nuevo.isNotBlank()) {
                    SessionManager.nombre = nuevo
                    pintarDatos()
                    Toast.makeText(requireContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Pon la de antes para poner la nueva, chiavo
    private fun dialogoCambiarPassword() {
        val etActual = EditText(requireContext()).apply {
            hint = "Contraseña actual"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val etNueva = EditText(requireContext()).apply {
            hint = "Nueva contraseña (mín. 6)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val contenedor = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
            addView(etActual)
            addView(etNueva)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar contraseña")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                when {
                    etActual.text.toString() != SessionManager.password ->
                        Toast.makeText(requireContext(), "La contraseña actual no coincide", Toast.LENGTH_LONG).show()
                    etNueva.text.toString().length < 6 ->
                        Toast.makeText(requireContext(), "La nueva contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_LONG).show()
                    else -> {
                        SessionManager.password = etNueva.text.toString()
                        Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
