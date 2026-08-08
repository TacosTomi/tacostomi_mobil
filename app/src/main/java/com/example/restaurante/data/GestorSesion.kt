package com.example.restaurante.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object GestorSesion {
    var nombre: String = "Cliente Demo"
    var correo: String = "clientedemo@example.com"
    var password: String = "Chemo123"
    var sesionActiva = false

    private const val PREFS_NAME = "RestaurantePrefs"
    private const val KEY_USUARIOS = "lista_usuarios"
    private const val KEY_USUARIO_ACTIVO = "usuario_activo"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun obtenerUsuarios(context: Context): JSONArray {
        val prefs = getPrefs(context)
        val jsonString = prefs.getString(KEY_USUARIOS, "[]")
        return JSONArray(jsonString)
    }

    private fun guardarUsuarios(context: Context, usuarios: JSONArray) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_USUARIOS, usuarios.toString()).apply()
    }

    fun inicializar(context: Context) {
        val prefs = getPrefs(context)
        val activoString = prefs.getString(KEY_USUARIO_ACTIVO, null)
        if (activoString != null) {
            val json = JSONObject(activoString)
            nombre = json.optString("nombre", "")
            correo = json.optString("correo", "")
            password = json.optString("password", "")
            sesionActiva = true
        } else {
            sesionActiva = false
        }
    }

    fun iniciarSesion(context: Context, correoIngresado: String, contrasenaIngresada: String): Boolean {
        val usuarios = obtenerUsuarios(context)
        for (i in 0 until usuarios.length()) {
            val u = usuarios.getJSONObject(i)
            if (u.optString("correo") == correoIngresado && u.optString("password") == contrasenaIngresada) {
                nombre = u.optString("nombre")
                correo = u.optString("correo")
                password = u.optString("password")
                sesionActiva = true
                guardarUsuarioActivo(context, u)
                return true
            }
        }
        return false
    }

    fun registrar(context: Context, nombreIn: String, correoIn: String, passIn: String): Boolean {
        val usuarios = obtenerUsuarios(context)
        for (i in 0 until usuarios.length()) {
            if (usuarios.getJSONObject(i).optString("correo") == correoIn) {
                return false 
            }
        }

        val nuevoUsuario = JSONObject().apply {
            put("nombre", nombreIn)
            put("correo", correoIn)
            put("password", passIn)
        }
        usuarios.put(nuevoUsuario)
        guardarUsuarios(context, usuarios)

        nombre = nombreIn
        correo = correoIn
        password = passIn
        sesionActiva = true
        guardarUsuarioActivo(context, nuevoUsuario)
        return true
    }

    private fun guardarUsuarioActivo(context: Context, usuario: JSONObject?) {
        val prefs = getPrefs(context)
        if (usuario != null) {
            prefs.edit().putString(KEY_USUARIO_ACTIVO, usuario.toString()).apply()
        } else {
            prefs.edit().remove(KEY_USUARIO_ACTIVO).apply()
        }
    }

    fun cerrarSesion(context: Context) {
        sesionActiva = false
        guardarUsuarioActivo(context, null)
    }
}
