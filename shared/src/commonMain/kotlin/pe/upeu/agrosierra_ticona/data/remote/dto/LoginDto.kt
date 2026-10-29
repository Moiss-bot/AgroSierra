package pe.upeu.agrosierra_ticona.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username")
    val usuario: String,

    @SerialName("password")
    val contrasena: String
)

@Serializable
data class LoginResponseDto(
    val token: String,

    @SerialName("username")
    val usuario: String,

    @SerialName("roles")
    val roles: Set<String>,

    @SerialName("permisos")
    val privilegios: Set<String>,

    val nombreCompleto: String = "",
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long = 3600
) {
    val rol: String
        get() = roles.firstOrNull().orEmpty()
}
