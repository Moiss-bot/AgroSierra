
package pe.upeu.agrosierra_ticona.domain.repository

import kotlinx.coroutines.flow.Flow

import pe.upeu.agrosierra_ticona.domain.model.Labor

interface LaborRepository {

    // Observar labores de un lote
    fun observarLaboresPorLote(
        loteId: Long
    ): Flow<List<Labor>>

    // Obtener una labor por su ID
    suspend fun obtenerLaborPorId(
        id: Long
    ): Labor?

    // Registrar una nueva labor
    suspend fun registrarLabor(
        labor: Labor
    ): Result<Labor>

    // Actualizar una labor existente
    suspend fun actualizarLabor(
        labor: Labor
    ): Result<Labor>

    // Eliminar una labor
    suspend fun eliminarLabor(
        id: Long
    ): Result<Unit>

    // Sincronizar operaciones pendientes
    suspend fun sincronizarLabores()
}
