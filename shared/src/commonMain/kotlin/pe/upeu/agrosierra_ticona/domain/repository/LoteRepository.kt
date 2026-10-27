package pe.upeu.agrosierra_ticona.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.upeu.agrosierra_ticona.domain.model.Lote

interface LoteRepository {

    fun observarLotes(): Flow<List<Lote>>

    fun observarLotePorId(id: Long): Flow<Lote?>

    suspend fun sincronizarLotes()

    suspend fun cerrarCampania(id: Long): Result<Lote>
}