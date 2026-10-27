package pe.upeu.agrosierra_ticona.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository

class ObtenerLotesUseCase(
    private val repository: LoteRepository
) {
    operator fun invoke(): Flow<List<Lote>> {
        return repository.observarLotes()
    }
}
