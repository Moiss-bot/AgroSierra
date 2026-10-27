
package pe.upeu.agrosierra_ticona.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import pe.upeu.agrosierra_ticona.data.remote.dto.LoteDto
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.put
import io.ktor.client.request.delete
import pe.upeu.agrosierra_ticona.data.remote.dto.LaborDto

class AgroApiService(
    private val client: HttpClient
) {

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
            }

            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(
                    Exception(
                        "Error al obtener lotes: HTTP ${response.status.value}"
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
                        "Error al obtener lote: HTTP ${response.status.value}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cerrarCampania(
        id: Long,
        token: String
    ): Result<LoteDto> {
        return try {
            val response: HttpResponse = client.post(
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
                        "Error al cerrar campaña: HTTP ${response.status.value}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
                    mapOf(
                        "usuario" to usuario,
                        "contrasena" to contrasena
                    )
                )
            }

            if (response.status.isSuccess()) {
                val resultado: Map<String, String> = response.body()
                val token = resultado["token"]

                if (!token.isNullOrBlank()) {
                    Result.success(token)
                } else {
                    Result.failure(
                        Exception("La respuesta no contiene un token JWT")
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        "Error de autenticación: HTTP ${response.status.value}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerLaboresPorLote(
        loteId: Long,
        token: String
    ): Result<List<LaborDto>> {
        return try {
            val response = client.get(
                ApiConfig.BASE_URL + ApiConfig.laboresPorLote(loteId)
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(
                    Exception("Error al obtener labores: HTTP ${response.status.value}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarLabor(
        labor: LaborDto,
        token: String
    ): Result<LaborDto> {
        return try {
            val response = client.post(
                ApiConfig.BASE_URL + ApiConfig.LABORES
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(labor)
            }

            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(
                    Exception("Error al registrar labor: HTTP ${response.status.value}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarLabor(
        id: Long,
        labor: LaborDto,
        token: String
    ): Result<LaborDto> {
        return try {
            val response = client.put(
                ApiConfig.BASE_URL + ApiConfig.laborPorId(id)
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(labor)
            }

            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                Result.failure(
                    Exception("Error al actualizar labor: HTTP ${response.status.value}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarLabor(
        id: Long,
        token: String
    ): Result<Unit> {
        return try {
            val response = client.delete(
                ApiConfig.BASE_URL + ApiConfig.laborPorId(id)
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Error al eliminar labor: HTTP ${response.status.value}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
