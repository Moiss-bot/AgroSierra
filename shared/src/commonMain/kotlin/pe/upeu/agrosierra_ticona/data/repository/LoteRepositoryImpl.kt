package pe.upeu.agrosierra_ticona.data.repository

import pe.upeu.agrosierraticona.`data`.local.Lote as LoteEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase
import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository

class LoteRepositoryImpl(
    private val database: AgroSierraDatabase,
    private val apiService: AgroApiService,
    private val sesionRepository: SesionRepository
) : LoteRepository {

    private val queries = database.loteQueries

    override fun observarLotes(): Flow<List<Lote>> {
        return queries.seleccionarLotes()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { lista ->
                lista.map { it.toDomain() }
            }
    }

    override fun observarLotePorId(
        id: Long
    ): Flow<Lote?> {
        return queries.seleccionarLotePorId(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }


    override suspend fun sincronizarLotes() {

        val sesion = sesionRepository.obtenerSesion()
            ?: throw IllegalStateException(
                "No existe una sesión activa"
            )

        val resultado = apiService.obtenerLotes(sesion.token)

        val lotesRemotos = resultado.getOrElse { error ->
            throw error
        }

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

            val resultado = apiService.cerrarCampania(
                id = id,
                token = sesion.token
            )

            resultado.map { lote ->

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


    private fun LoteEntity.toDomain(): Lote {
        return Lote(
            id = id,
            codigo = codigo,
            nombre = nombre,
            hectareas = hectareas,
            cultivo = cultivo,
            valle = valle,
            campaniaCerrada = campaniaCerrada != 0L,
            dosisMaximaPorHectarea = dosisMaximaPorHectarea
        )
    }
}
