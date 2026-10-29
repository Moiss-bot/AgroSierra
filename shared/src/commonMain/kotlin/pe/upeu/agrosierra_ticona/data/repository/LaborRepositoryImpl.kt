
package pe.upeu.agrosierra_ticona.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

import pe.upeu.agrosierra_ticona.data.local.AgroSierraDatabase
import pe.upeu.agrosierra_ticona.data.remote.AgroApiService
import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.model.TipoLabor
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.SesionRepository
import pe.upeu.agrosierra_ticona.domain.usecase.ValidarLaborUseCase

class LaborRepositoryImpl(
    private val database: AgroSierraDatabase,
    private val loteRepository: LoteRepository,
    private val validarLabor: ValidarLaborUseCase,
    private val apiService: AgroApiService,
    private val sesionRepository: SesionRepository
) : LaborRepository {

    // Adaptado para utilizar AgroDb.sq
    private val queries = database.agroDbQueries

    // ==========================================
    // 1. OBSERVAR LABORES POR LOTE
    // ==========================================

    override fun observarLaboresPorLote(
        loteId: Long
    ): Flow<List<Labor>> {

        return queries
            .seleccionarLaboresPorLote(loteId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { registros ->
                registros.map { registro ->
                    Labor(
                        id = registro.id,
                        loteId = registro.loteId,
                        tipo = TipoLabor.valueOf(registro.tipo),
                        fecha = registro.fecha,
                        responsable = registro.responsable,
                        cantidad = registro.cantidad,
                        observacion = registro.observacion,
                        pendiente = registro.pendiente != 0L
                    )
                }
            }
    }

    // ==========================================
    // 2. OBTENER LABOR POR ID
    // ==========================================

    override suspend fun obtenerLaborPorId(
        id: Long
    ): Labor? {

        val registro = queries
            .seleccionarLaborActivaPorId(id)
            .executeAsOneOrNull()
            ?: return null

        return Labor(
            id = registro.id,
            loteId = registro.loteId,
            tipo = TipoLabor.valueOf(registro.tipo),
            fecha = registro.fecha,
            responsable = registro.responsable,
            cantidad = registro.cantidad,
            observacion = registro.observacion,
            pendiente = registro.pendiente != 0L
        )
    }

    // ==========================================
    // 3. REGISTRAR LABOR
    // ==========================================

    override suspend fun registrarLabor(
        labor: Labor
    ): Result<Labor> {

        return try {

            val lote = loteRepository
                .observarLotePorId(labor.loteId)
                .first()
                ?: return Result.failure(
                    IllegalArgumentException(
                        "No se encontró el lote seleccionado"
                    )
                )

            val validacion = validarLabor(labor, lote)

            if (validacion.isFailure) {
                return Result.failure(
                    validacion.exceptionOrNull()
                        ?: IllegalArgumentException(
                            "La labor no cumple las reglas de negocio"
                        )
                )
            }

            var laborGuardada: Labor? = null

            // Generar ID e insertar de forma atómica
            database.transaction {

                val nuevoId = queries
                    .obtenerSiguienteIdLocal()
                    .executeAsOne()

                val laborPendiente = labor.copy(
                    id = nuevoId,
                    pendiente = true
                )

                queries.insertarLabor(
                    id = laborPendiente.id,
                    loteId = laborPendiente.loteId,
                    tipo = laborPendiente.tipo.name,
                    fecha = laborPendiente.fecha,
                    responsable = laborPendiente.responsable,
                    cantidad = laborPendiente.cantidad,
                    observacion = laborPendiente.observacion,
                    pendiente = 1L,
                    eliminado = 0L,
                    operacionPendiente = "CREAR"
                )

                laborGuardada = laborPendiente
            }

            Result.success(
                laborGuardada
                    ?: error("No se pudo guardar la labor")
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 4. ACTUALIZAR LABOR
    // ==========================================

    override suspend fun actualizarLabor(
        labor: Labor
    ): Result<Labor> {

        return try {

            val existente = queries
                .seleccionarLaborActivaPorId(labor.id)
                .executeAsOneOrNull()
                ?: return Result.failure(
                    IllegalArgumentException(
                        "No se encontró la labor que desea actualizar"
                    )
                )

            if (existente.loteId != labor.loteId) {
                return Result.failure(
                    IllegalArgumentException(
                        "No se puede cambiar el lote de la labor"
                    )
                )
            }

            val lote = loteRepository
                .observarLotePorId(labor.loteId)
                .first()
                ?: return Result.failure(
                    IllegalArgumentException(
                        "No se encontró el lote"
                    )
                )

            val validacion = validarLabor(labor, lote)

            if (validacion.isFailure) {
                return Result.failure(
                    validacion.exceptionOrNull()
                        ?: IllegalArgumentException(
                            "La labor no cumple las reglas de negocio"
                        )
                )
            }

            val operacion = if (
                existente.operacionPendiente == "CREAR"
            ) {
                "CREAR"
            } else {
                "ACTUALIZAR"
            }

            val actualizada = labor.copy(
                pendiente = true
            )

            queries.insertarLabor(
                id = actualizada.id,
                loteId = actualizada.loteId,
                tipo = actualizada.tipo.name,
                fecha = actualizada.fecha,
                responsable = actualizada.responsable,
                cantidad = actualizada.cantidad,
                observacion = actualizada.observacion,
                pendiente = 1L,
                eliminado = 0L,
                operacionPendiente = operacion
            )

            Result.success(actualizada)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 5. ELIMINAR LABOR
    // ==========================================

    override suspend fun eliminarLabor(
        id: Long
    ): Result<Unit> {

        return try {

            val labor = queries
                .seleccionarLaborActivaPorId(id)
                .executeAsOneOrNull()
                ?: return Result.failure(
                    IllegalArgumentException(
                        "No se encontró la labor"
                    )
                )

            val lote = loteRepository
                .observarLotePorId(labor.loteId)
                .first()
                ?: return Result.failure(
                    IllegalArgumentException(
                        "No se encontró el lote"
                    )
                )

            if (lote.campaniaCerrada) {
                return Result.failure(
                    IllegalStateException(
                        "No se pueden eliminar labores " +
                                "de una campaña cerrada"
                    )
                )
            }

            queries.marcarLaborEliminada(id)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // 6. SINCRONIZAR LABORES
    // ==========================================

    override suspend fun sincronizarLabores() {

        val pendientes = queries
            .seleccionarLaboresPendientes()
            .executeAsList()

        // Descartar labores creadas y eliminadas
        // antes de llegar al servidor.
        pendientes
            .filter {
                it.operacionPendiente == "DESCARTAR"
            }
            .forEach { registro ->
                queries.eliminarLabor(registro.id)
            }

        // Las operaciones remotas se implementarán
        // cuando AgroAPI esté disponible.
        //
        // CREAR -> POST
        // ACTUALIZAR -> PUT
        // ELIMINAR -> DELETE
        //
        // No se envían IDs locales negativos
        // hasta definir el contrato con el backend.
    }
}
