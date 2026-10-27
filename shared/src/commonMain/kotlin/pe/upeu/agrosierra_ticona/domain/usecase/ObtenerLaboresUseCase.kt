package pe.upeu.agrosierra_ticona.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

class ObtenerLaboresUseCase(
    private val repository: LaborRepository
) {
    operator fun invoke(
        loteId: Long
    ): Flow<List<Labor>> {
        return repository.observarLaboresPorLote(loteId)
    }
}
