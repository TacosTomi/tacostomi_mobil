package com.example.restaurante.network.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val correo: String,
    val password: String
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

