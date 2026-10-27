package pe.upeu.agrosierra_ticona.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val usuario: String,
    val contrasena: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val usuario: String,
    val rol: String,
    val privilegios: Set<String>
)
