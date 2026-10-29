
package pe.upeu.agrosierra_ticona.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

import kotlinx.serialization.Serializable

import pe.upeu.agrosierra_ticona.data.remote.dto.LaborDto
import pe.upeu.agrosierra_ticona.data.remote.dto.LoteDto
import pe.upeu.agrosierra_ticona.data.remote.dto.LoginRequestDto
import pe.upeu.agrosierra_ticona.data.remote.dto.LoginResponseDto

@Serializable
data class PaginaResponseDto<T>(
    val contenido: List<T>,
    val pagina: Int,
    val tamanio: Int,
    val totalElementos: Long,
    val totalPaginas: Int,
    val ultima: Boolean
)

class AgroApiService(
    private val client: HttpClient
) {

    // ========================================
    // AUTENTICACIÓN
    // ========================================

    suspend fun iniciarSesion(
        usuario: String,
        contrasena: String
    ): Result<String> {

        return try {

            val response = client.post(
                ApiConfig.BASE_URL + ApiConfig.LOGIN
            ) {
                contentType(ContentType.Application.Json)

                setBody(
                    LoginRequestDto(
                        usuario = usuario.trim(),
                        contrasena = contrasena
                    )
                )
            }

            if (response.status.isSuccess()) {

                val resultado: LoginResponseDto = response.body()

                if (resultado.token.isNotBlank()) {
                    Result.success(resultado.token)
                } else {
                    Result.failure(
                        Exception(
                            "La respuesta no contiene un token JWT"
                        )
                    )
                }

            } else {

                val mensaje = when (response.status.value) {
                    400 -> "Datos de acceso inválidos"
                    401 -> "Usuario o contraseña incorrectos"
                    403 -> "Usuario sin acceso autorizado"
                    else -> "Error de autenticación: HTTP ${response.status.value}"
                }

                Result.failure(Exception(mensaje))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Permite recuperar también los roles y permisos
    // sin modificar el método de login existente.

    suspend fun iniciarSesionCompleta(
        usuario: String,
        contrasena: String
    ): Result<LoginResponseDto> {

        return try {

            val response = client.post(
                ApiConfig.BASE_URL + ApiConfig.LOGIN
            ) {
                contentType(ContentType.Application.Json)

                setBody(
                    LoginRequestDto(
                        usuario = usuario.trim(),
                        contrasena = contrasena
                    )
                )
            }

            if (response.status.isSuccess()) {

                val resultado: LoginResponseDto = response.body()

                if (resultado.token.isNotBlank()) {
                    Result.success(resultado)
                } else {
                    Result.failure(
                        Exception("Token JWT vacío")
                    )
                }

            } else {

                val mensaje = when (response.status.value) {
                    401 -> "Usuario o contraseña incorrectos"
                    403 -> "Acceso no autorizado"
                    else -> "Error HTTP ${response.status.value}"
                }

                Result.failure(Exception(mensaje))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // LOTES
    // ========================================

    suspend fun obtenerLotes(
        token: String
    ): Result<List<LoteDto>> {

        return try {

            val response = client.get(
                ApiConfig.BASE_URL + ApiConfig.LOTES
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )

                parameter("pagina", 0)
                parameter("tamanio", 100)
            }

            if (response.status.isSuccess()) {

                val resultado:
                        PaginaResponseDto<LoteDto> = response.body()

                Result.success(resultado.contenido)

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "obtener lotes",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerLotePorId(
        id: Long,
        token: String
    ): Result<LoteDto> {

        return try {

            val response = client.get(
                ApiConfig.BASE_URL + ApiConfig.lotePorId(id)
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            if (response.status.isSuccess()) {

                Result.success(response.body())

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "obtener lote",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // CIERRE DE CAMPAÑA
    // ========================================

    suspend fun cerrarCampania(
        id: Long,
        token: String
    ): Result<LoteDto> {

        return try {

            val response = client.post(
                ApiConfig.BASE_URL + ApiConfig.cerrarCampania(id)
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            if (response.status.isSuccess()) {

                Result.success(response.body())

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "cerrar campaña",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // LABORES POR LOTE
    // ========================================

    suspend fun obtenerLaboresPorLote(
        loteId: Long,
        token: String
    ): Result<List<LaborDto>> {

        return try {

            val response = client.get(
                ApiConfig.BASE_URL + ApiConfig.laboresPorLote(loteId)
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )

                parameter("pagina", 0)
                parameter("tamanio", 100)
            }

            if (response.status.isSuccess()) {

                val resultado:
                        PaginaResponseDto<LaborDto> = response.body()

                Result.success(resultado.contenido)

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "obtener labores",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // REGISTRAR LABOR
    // ========================================

    suspend fun registrarLabor(
        labor: LaborDto,
        token: String
    ): Result<LaborDto> {

        return try {

            val response = client.post(
                ApiConfig.BASE_URL + ApiConfig.LABORES
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )

                contentType(ContentType.Application.Json)

                setBody(labor)
            }

            if (response.status.isSuccess()) {

                Result.success(response.body())

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "registrar labor",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // ACTUALIZAR LABOR
    // ========================================

    suspend fun actualizarLabor(
        id: Long,
        labor: LaborDto,
        token: String
    ): Result<LaborDto> {

        return try {

            val response = client.put(
                ApiConfig.BASE_URL + ApiConfig.laborPorId(id)
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )

                contentType(ContentType.Application.Json)

                setBody(labor)
            }

            if (response.status.isSuccess()) {

                Result.success(response.body())

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "actualizar labor",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // ELIMINAR LABOR
    // ========================================

    suspend fun eliminarLabor(
        id: Long,
        token: String
    ): Result<Unit> {

        return try {

            val response = client.delete(
                ApiConfig.BASE_URL + ApiConfig.laborPorId(id)
            ) {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }

            if (response.status.isSuccess()) {

                Result.success(Unit)

            } else {

                Result.failure(
                    Exception(
                        mensajeError(
                            "eliminar labor",
                            response.status.value
                        )
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========================================
    // MENSAJES DE ERROR
    // ========================================

    private fun mensajeError(
        operacion: String,
        codigo: Int
    ): String {

        return when (codigo) {

            400 -> "Datos inválidos al $operacion"

            401 -> "Tu sesión ha expirado. Inicia sesión nuevamente"

            403 -> "No tienes permisos para $operacion"

            404 -> "El recurso solicitado no existe"

            409 -> "La operación no cumple las reglas del negocio"

            500 -> "Error interno del servidor"

            else -> "Error al $operacion: HTTP $codigo"
        }
    }
}
