package com.example.restaurante.data

import android.content.Context
import android.content.SharedPreferences
import com.example.restaurante.network.RetrofitClient
import com.example.restaurante.network.dto.LoginRequest

sealed class ResultadoLogin {
    data class Exito(val nombre: String) : ResultadoLogin()
    data class Error(val mensaje: String) : ResultadoLogin()
}

object GestorSesion {
    var id: Int = -1
    var nombre: String = ""
    var correo: String = ""
    var rolId: Int = -1
    var token: String? = null
    var sesionActiva = false

    private const val PREFS_NAME = "RestaurantePrefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_ID = "usuario_id"
    private const val KEY_NOMBRE = "usuario_nombre"
    private const val KEY_CORREO = "usuario_correo"
    private const val KEY_ROL = "usuario_rol"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun inicializar(context: Context) {
        val prefs = getPrefs(context)
        val tokenGuardado = prefs.getString(KEY_TOKEN, null)
        if (tokenGuardado != null) {
            token = tokenGuardado
            id = prefs.getInt(KEY_ID, -1)
            nombre = prefs.getString(KEY_NOMBRE, "") ?: ""
            correo = prefs.getString(KEY_CORREO, "") ?: ""
            rolId = prefs.getInt(KEY_ROL, -1)
            sesionActiva = true
        } else {
            sesionActiva = false
        }
    }

    /**
     * Llama al endpoint real POST /login. Debe invocarse desde una corrutina
     * (por ejemplo con lifecycleScope.launch { }), ya que hace una petición de red.
     */
    suspend fun iniciarSesion(context: Context, correoIngresado: String, contrasenaIngresada: String): ResultadoLogin {
        return try {
            val respuesta = RetrofitClient.api.login(LoginRequest(correoIngresado, contrasenaIngresada))

            if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                val data = respuesta.body()!!.data!!
                setSesion(
                    context,
                    data.token,
                    data.usuario.id,
                    data.usuario.nombre,
                    data.usuario.correo,
                    data.usuario.rolId
                )
                ResultadoLogin.Exito(nombre)
            } else {
                val mensaje = respuesta.body()?.mensaje ?: "Credenciales incorrectas"
                ResultadoLogin.Error(mensaje)
            }
        } catch (e: Exception) {
            ResultadoLogin.Error("No se pudo conectar con el servidor: ${e.message}")
        }
    }

    fun setSesion(context: Context, nuevoToken: String, nuevoId: Int, nuevoNombre: String, nuevoCorreo: String, nuevoRolId: Int = 2) {
        token = nuevoToken
        id = nuevoId
        nombre = nuevoNombre
        correo = nuevoCorreo
        rolId = nuevoRolId
        sesionActiva = true
        guardarSesion(context)
    }

    fun guardarSesion(context: Context) {
        getPrefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_ID, id)
            .putString(KEY_NOMBRE, nombre)
            .putString(KEY_CORREO, correo)
            .putInt(KEY_ROL, rolId)
            .apply()
    }

    fun cerrarSesion(context: Context) {
        sesionActiva = false
        token = null
        getPrefs(context).edit().clear().apply()
    }
}
