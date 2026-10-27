package pe.upeu.agrosierra_ticona.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaborDto(
    val id: Long,
    val loteId: Long,
    val tipo: String,
    val fecha: String,
    val responsable: String,
    val cantidad: Double,
    val observacion: String,
    val pendiente: Boolean = false
)
