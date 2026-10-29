
package pe.upeu.agrosierra_ticona.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase
import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

class LoteRepositoryImpl(
    private val database: AgroSierraDatabase,
    private val apiService: AgroApiService,
    private val sesionRepository: SesionRepository
) : LoteRepository {

    private val queries = database.agroDbQueries

    override fun observarLotes(): Flow<List<Lote>> {

        return queries
            .seleccionarLotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { registros ->

                registros.map { registro ->

                    Lote(
                        id = registro.id,
                        codigo = registro.codigo,
                        nombre = registro.nombre,
                        hectareas = registro.hectareas,
                        cultivo = registro.cultivo,
                        valle = registro.valle,
                        campaniaCerrada =
                            registro.campaniaCerrada != 0L,
                        dosisMaximaPorHectarea =
                            registro.dosisMaximaPorHectarea
                    )
                }
            }
    }

    override fun observarLotePorId(id: Long): Flow<Lote?> {

        return queries
            .seleccionarLotePorId(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { registro ->

                registro?.let {

                    Lote(
                        id = it.id,
                        codigo = it.codigo,
                        nombre = it.nombre,
                        hectareas = it.hectareas,
                        cultivo = it.cultivo,
                        valle = it.valle,
                        campaniaCerrada =
                            it.campaniaCerrada != 0L,
                        dosisMaximaPorHectarea =
                            it.dosisMaximaPorHectarea
                    )
                }
            }
    }

    override suspend fun sincronizarLotes() {

        val sesion = sesionRepository.obtenerSesion()
            ?: throw IllegalStateException(
                "No existe una sesión activa"
            )

        val lotesRemotos = apiService
            .obtenerLotes(sesion.token)
            .getOrElse { throw it }

        database.transaction {

            queries.eliminarLotes()

            lotesRemotos.forEach { lote ->

                queries.insertarLote(
                    id = lote.id,
                    codigo = lote.codigo,
                    nombre = lote.nombre,
                    hectareas = lote.hectareas,
                    cultivo = lote.cultivo,
                    valle = lote.valle,
                    campaniaCerrada =
                        if (lote.campaniaCerrada) 1L else 0L,
                    dosisMaximaPorHectarea =
                        lote.dosisMaximaPorHectarea
                )
            }
        }
    }

    override suspend fun cerrarCampania(
        id: Long
    ): Result<Lote> {

        return try {

            val sesion = sesionRepository.obtenerSesion()
                ?: return Result.failure(
                    IllegalStateException(
                        "Debe iniciar sesión para cerrar una campaña"
                    )
                )

            apiService
                .cerrarCampania(
                    id = id,
                    token = sesion.token
                )
                .map { lote ->

                    queries.insertarLote(
                        id = lote.id,
                        codigo = lote.codigo,
                        nombre = lote.nombre,
                        hectareas = lote.hectareas,
                        cultivo = lote.cultivo,
                        valle = lote.valle,
                        campaniaCerrada =
                            if (lote.campaniaCerrada) 1L else 0L,
                        dosisMaximaPorHectarea =
                            lote.dosisMaximaPorHectarea
                    )

                    Lote(
                        id = lote.id,
                        codigo = lote.codigo,
                        nombre = lote.nombre,
                        hectareas = lote.hectareas,
                        cultivo = lote.cultivo,
                        valle = lote.valle,
                        campaniaCerrada = lote.campaniaCerrada,
                        dosisMaximaPorHectarea =
                            lote.dosisMaximaPorHectarea
                    )
                }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
