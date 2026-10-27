
package pe.upeu.agrosierra_ticona.presentation.detallelote

import pe.upeu.agrosierra_ticona.domain.model.Lote
import pe.upeu.agrosierra_ticona.domain.model.Labor

data class DetalleLoteUiState(
    val lote: Lote? = null,
    val labores: List<Labor> = emptyList(),
    val cargando: Boolean = false,
    val procesando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)

