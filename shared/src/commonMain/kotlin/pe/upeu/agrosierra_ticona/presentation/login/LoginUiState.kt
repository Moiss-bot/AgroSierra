package pe.upeu.agrosierra_ticona.presentation.login

data class LoginUiState(
    val usuario: String = "",
    val contrasena: String = "",
    val mostrarContrasena: Boolean = false,
    val cargando: Boolean = false,
    val mensajeError: String? = null,
    val autenticado: Boolean = false
)
