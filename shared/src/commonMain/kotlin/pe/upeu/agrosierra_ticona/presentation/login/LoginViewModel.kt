
package pe.upeu.agrosierra_ticona.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

class LoginViewModel(
    private val sesionRepository: SesionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun cambiarUsuario(valor: String) {
        _uiState.value = _uiState.value.copy(
            usuario = valor,
            mensajeError = null
        )
    }

    fun cambiarContrasena(valor: String) {
        _uiState.value = _uiState.value.copy(
            contrasena = valor,
            mensajeError = null
        )
    }

    fun alternarVisibilidadContrasena() {
        _uiState.value = _uiState.value.copy(
            mostrarContrasena = !_uiState.value.mostrarContrasena
        )
    }

    fun iniciarSesion() {

        val estado = _uiState.value

        if (estado.cargando) return

        if (estado.usuario.isBlank()) {
            _uiState.value = estado.copy(
                mensajeError = "Ingrese su usuario."
            )
            return
        }

        if (estado.contrasena.isBlank()) {
            _uiState.value = estado.copy(
                mensajeError = "Ingrese su contraseña."
            )
            return
        }

        _uiState.value = estado.copy(
            cargando = true,
            autenticado = false,
            mensajeError = null
        )

        viewModelScope.launch {

            val resultado = sesionRepository.iniciarSesion(
                usuario = estado.usuario.trim(),
                contrasena = estado.contrasena
            )

            resultado.fold(

                onSuccess = { sesion ->

                    if (sesion.token.isNotBlank()) {

                        _uiState.value = _uiState.value.copy(
                            cargando = false,
                            autenticado = true,
                            mensajeError = null
                        )

                    } else {

                        _uiState.value = _uiState.value.copy(
                            cargando = false,
                            autenticado = false,
                            mensajeError = "El servidor no devolvió un token JWT."
                        )
                    }
                },

                onFailure = { error ->

                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        autenticado = false,
                        mensajeError = error.message
                            ?: "No se pudo iniciar sesión."
                    )
                }
            )
        }
    }
}
