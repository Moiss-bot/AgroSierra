package pe.upeu.agrosierra_ticona.presentation.lotes

import pe.upeu.agrosierra_ticona.domain.model.Lote

data class LotesUiState(
    val lotes: List<Lote> = emptyList(),
    val cargando: Boolean = false,
    val sincronizando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)
