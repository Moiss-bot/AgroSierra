
package pe.upeu.agrosierra_ticona.presentation.laborform

import pe.upeu.agrosierra_ticona.domain.model.TipoLabor

data class LaborFormUiState(
    val laborId: Long? = null,
    val loteId: Long = 0L,
    val tipo: TipoLabor = TipoLabor.RIEGO,
    val fecha: String = "",
    val responsable: String = "",
    val cantidad: String = "",
    val observacion: String = "",
    val editando: Boolean = false,
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardadoCorrectamente: Boolean = false,
    val mensajeError: String? = null
)
