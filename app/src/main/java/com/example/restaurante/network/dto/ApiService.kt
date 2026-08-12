package com.example.restaurante.network

import com.example.restaurante.network.dto.ApiResponse
import com.example.restaurante.network.dto.CategoriaDto
import com.example.restaurante.network.dto.CrearPedidoRequest
import com.example.restaurante.network.dto.LoginData
import com.example.restaurante.network.dto.LoginRequest
import com.example.restaurante.network.dto.PedidoDto
import com.example.restaurante.network.dto.PlatilloDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

    @GET("categorias")
    suspend fun obtenerCategorias(): Response<List<CategoriaDto>>

    @GET("platillos")
    suspend fun obtenerPlatillos(): Response<ApiResponse<List<PlatilloDto>>>

    @POST("crearPedido")
    suspend fun crearPedido(@Body request: CrearPedidoRequest): Response<ApiResponse<PedidoDto>>

    @GET("pedidos/cliente/{clienteId}")
    suspend fun obtenerPedidosPorCliente(@Path("clienteId") clienteId: Int): Response<ApiResponse<List<PedidoDto>>>

    @GET("pedidos/{id}")
    suspend fun obtenerPedidoPorId(@Path("id") id: Int): Response<ApiResponse<PedidoDto>>
}
