
package pe.upeu.agrosierra_ticona.domain.usecase

import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.model.TipoLabor

class ValidarLaborUseCase {

    operator fun invoke(
        labor: Labor,
        lote: Lote
    ): Result<Unit> {

        if (labor.loteId != lote.id) {
            return Result.failure(
                IllegalArgumentException(
                    "La labor no pertenece al lote seleccionado"
                )
            )
        }

        if (lote.campaniaCerrada) {
            return Result.failure(
                IllegalStateException(
                    "No se pueden registrar ni modificar labores en una campaña cerrada"
                )
            )
        }

        if (labor.responsable.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Debe ingresar el responsable de la labor"
                )
            )
        }

        if (!labor.cantidad.isFinite() || labor.cantidad <= 0.0) {
            return Result.failure(
                IllegalArgumentException(
                    "La cantidad debe ser un número válido mayor que cero"
                )
            )
        }

        if (labor.fecha.isBlank()) {
            return Result.failure(
                IllegalArgumentException(
                    "Debe ingresar la fecha de la labor"
                )
            )
        }

        if (labor.tipo == TipoLabor.FUMIGACION) {

            if (!lote.hectareas.isFinite() ||
                lote.hectareas <= 0.0 ||
                !lote.dosisMaximaPorHectarea.isFinite() ||
                lote.dosisMaximaPorHectarea <= 0.0
            ) {
                return Result.failure(
                    IllegalArgumentException(
                        "El lote no tiene una superficie o dosis máxima válida"
                    )
                )
            }

            val dosisMaxima =
                lote.hectareas * lote.dosisMaximaPorHectarea

            if (labor.cantidad > dosisMaxima) {
                return Result.failure(
                    IllegalArgumentException(
                        "La fumigación supera la dosis máxima permitida: $dosisMaxima"
                    )
                )
            }
        }

        return Result.success(Unit)
    }
}
