package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

class EliminarLaborUseCase(
    private val repository: LaborRepository
) {
    suspend operator fun invoke(
        id: Long
    ): Result<Unit> {
        return repository.eliminarLabor(id)
    }
}
