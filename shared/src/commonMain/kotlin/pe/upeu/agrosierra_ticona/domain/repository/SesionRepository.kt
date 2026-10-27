package pe.upeu.agrosierra_ticona.domain.repository

import pe.upeu.agrosierra_ticona.domain.model.Sesion

interface SesionRepository {

    suspend fun iniciarSesion(
        usuario: String,
        contrasena: String
    ): Result<Sesion>

    suspend fun obtenerSesion(): Sesion?

    suspend fun cerrarSesion()

    suspend fun obtenerUsuarioActual(): Result<Sesion>
}