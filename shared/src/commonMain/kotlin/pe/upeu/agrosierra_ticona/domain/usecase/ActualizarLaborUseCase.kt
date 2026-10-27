package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

class ActualizarLaborUseCase(
    private val repository: LaborRepository
) {
    suspend operator fun invoke(
        labor: Labor
    ): Result<Labor> {
        return repository.actualizarLabor(labor)
    }
}
