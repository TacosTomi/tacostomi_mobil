package com.example.restaurante.network.dto

data class ApiResponse<T>(
    val exito: Boolean = false,
    val data: T? = null,
    val mensaje: String? = null
)
