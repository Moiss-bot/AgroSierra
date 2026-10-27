package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.model.Sesion
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

class IniciarSesionUseCase(
    private val repository: SesionRepository
) {
    suspend operator fun invoke(
        usuario: String,
        contrasena: String
    ): Result<Sesion> {
        return repository.iniciarSesion(
            usuario,
            contrasena
        )
    }
}
