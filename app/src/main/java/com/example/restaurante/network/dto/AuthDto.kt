package com.example.restaurante.network.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RegistroRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("rol_id") val rolId: Int = 2 // 2 para Clientes
)

data class CambioPasswordRequest(
    @SerializedName("password_actual") val passwordActual: String,
    @SerializedName("password_nuevo") val passwordNuevo: String,
    @SerializedName("password_nuevo_confirmation") val passwordNuevoConfirmation: String
)

data class LoginData(
    val token: String,
    val usuario: UsuarioDto
)

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val correo: String,
    @SerializedName("rol_id") val rolId: Int
)

