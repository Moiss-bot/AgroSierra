package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository

class CerrarCampaniaUseCase(
    private val repository: LoteRepository
) {
    suspend operator fun invoke(
        loteId: Long
    ): Result<Lote> {
        return repository.cerrarCampania(loteId)
    }
}
