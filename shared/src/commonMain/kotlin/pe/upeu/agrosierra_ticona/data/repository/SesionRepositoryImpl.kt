
package pe.upeu.agrosierra_ticona.data.repository

import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.domain.model.Sesion
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

class SesionRepositoryImpl(
    private val apiService: AgroApiService
) : SesionRepository {

    private var sesionActual: Sesion? = null

    override suspend fun iniciarSesion(
        usuario: String,
        contrasena: String
    ): Result<Sesion> {

        val resultado = apiService.iniciarSesion(
            usuario = usuario,
            contrasena = contrasena
        )

        return resultado.map { token ->

            val sesion = Sesion(
                token = token,
                usuario = usuario,
                rol = "",
                privilegios = emptySet()
            )

            sesionActual = sesion

            sesion
        }
    }

    override suspend fun obtenerSesion(): Sesion? {
        return sesionActual
    }

    override suspend fun cerrarSesion() {
        sesionActual = null
    }

    override suspend fun obtenerUsuarioActual(): Result<Sesion> {

        val sesion = sesionActual

        return if (sesion != null) {
            Result.success(sesion)
        } else {
            Result.failure(
                IllegalStateException(
                    "No existe una sesión activa"
                )
            )
        }
    }
}
