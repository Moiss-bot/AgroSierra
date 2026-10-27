package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository

class SincronizarUseCase(
    private val laborRepository: LaborRepository,
    private val loteRepository: LoteRepository
) {
    suspend operator fun invoke() {

        // Primero sincronizamos las labores
        laborRepository.sincronizarLabores()

        // Después actualizamos los lotes
        loteRepository.sincronizarLotes()
    }
}
