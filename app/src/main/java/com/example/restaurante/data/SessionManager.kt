package com.example.restaurante.data

/**
 * Aquí guardamos quién eres por ahora, chiavo.
 * En lo real guardaríamos una llave segura, chiavo.
 * Usando cosas más pro de Android, chiavo.
 */
object SessionManager {
    var nombre: String = "Cliente Demo"
    var correo: String = ""
    var password: String = "123456"
    var sesionActiva = false

    fun iniciarSesion(correoIngresado: String) {
        correo = correoIngresado
        sesionActiva = true
    }

    fun registrar(nombreIn: String, correoIn: String, passIn: String) {
        nombre = nombreIn
        correo = correoIn
        password = passIn
        sesionActiva = true
    }

    fun cerrarSesion() {
        sesionActiva = false
    }
}
