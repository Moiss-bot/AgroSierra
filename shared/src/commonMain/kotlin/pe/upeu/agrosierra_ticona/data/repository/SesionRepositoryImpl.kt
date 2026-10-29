
package pe.upeu.agrosierra_ticona.data.repository

import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase
import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.domain.model.Sesion
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SesionRepositoryImpl(
    private val apiService: AgroApiService,
    private val database: AgroSierraDatabase
) : SesionRepository {

    private var sesionActual: Sesion? = null

    // ==========================================
    // 1. INICIAR SESIÓN
    // ==========================================

    @OptIn(ExperimentalTime::class)
    override suspend fun iniciarSesion(
        usuario: String,
        contrasena: String
    ): Result<Sesion> {

        return try {

            val respuesta = apiService.iniciarSesionCompleta(
                usuario = usuario,
                contrasena = contrasena
            ).getOrThrow()

            if (respuesta.token.isBlank()) {
                return Result.failure(
                    IllegalStateException(
                        "El servidor no devolvió un token JWT."
                    )
                )
            }

            val ahora = Clock.System.now()
                .toEpochMilliseconds()

            val duracion: Long = respuesta.expiraEnSegundos

            if (duracion <= 0L) {
                return Result.failure(
                    IllegalStateException(
                        "El servidor devolvió una sesión expirada."
                    )
                )
            }

            val fechaExpiracion = ahora + duracion * 1000L

            val sesion = Sesion(
                token = respuesta.token,
                usuario = respuesta.usuario,
                rol = respuesta.rol,
                privilegios = respuesta.privilegios,
                expiraEnSegundos = duracion,
                fechaExpiracion = fechaExpiracion
            )

            database.agroDbQueries.guardarSesion(
                id = 1L,
                token = sesion.token,
                usuario = sesion.usuario,
                rol = sesion.rol,
                privilegios = sesion.privilegios.joinToString(","),
                fechaExpiracion = sesion.fechaExpiracion
            )

            sesionActual = sesion

            Result.success(sesion)

        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    // ==========================================
    // 2. OBTENER SESIÓN GUARDADA
    // ==========================================

    @OptIn(ExperimentalTime::class)
    override suspend fun obtenerSesion(): Sesion? {

        val ahora = Clock.System.now()
            .toEpochMilliseconds()

        val sesionMemoria = sesionActual

        if (sesionMemoria != null) {

            if (!sesionMemoria.estaExpirada(ahora)) {
                return sesionMemoria
            }

            cerrarSesion()
            return null
        }

        val registro = database.agroDbQueries
            .obtenerSesion()
            .executeAsOneOrNull()
            ?: return null

        val privilegiosGuardados = registro.privilegios
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()

        val fechaExpiracion: Long = registro.fechaExpiracion

        val segundosRestantes: Long =
            ((fechaExpiracion - ahora) / 1000L)
                .coerceAtLeast(0L)

        val sesion = Sesion(
            token = registro.token,
            usuario = registro.usuario,
            rol = registro.rol,
            privilegios = privilegiosGuardados,
            expiraEnSegundos = segundosRestantes,
            fechaExpiracion = fechaExpiracion
        )

        if (sesion.estaExpirada(ahora)) {

            cerrarSesion()
            return null
        }

        sesionActual = sesion

        return sesion
    }

    // ==========================================
    // 3. CERRAR SESIÓN
    // ==========================================

    override suspend fun cerrarSesion() {

        sesionActual = null

        database.agroDbQueries.eliminarSesion()
    }

    // ==========================================
    // 4. OBTENER USUARIO ACTUAL
    // ==========================================

    override suspend fun obtenerUsuarioActual(): Result<Sesion> {

        return try {

            val sesion = obtenerSesion()

            if (sesion != null) {

                Result.success(sesion)

            } else {

                Result.failure(
                    IllegalStateException(
                        "No existe una sesión activa o ha expirado."
                    )
                )
            }

        } catch (error: Exception) {

            Result.failure(error)
        }
    }
}
