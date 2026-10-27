
package pe.upeu.agrosierra_ticona.data.mapper

import pe.upeu.agrosierra_ticona.data.remote.dto.LaborDto
import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.model.TipoLabor

// ==========================================
// CONVERTIR LABOR A DTO
// ==========================================

fun Labor.toDto(): LaborDto {
    return LaborDto(
        id = id,
        loteId = loteId,
        tipo = tipo.name,
        fecha = fecha,
        responsable = responsable,
        cantidad = cantidad,
        observacion = observacion,
        pendiente = pendiente
    )
}

// ==========================================
// CONVERTIR DTO A LABOR
// ==========================================

fun LaborDto.toDomain(): Labor {
    return Labor(
        id = id,
        loteId = loteId,
        tipo = TipoLabor.valueOf(tipo),
        fecha = fecha,
        responsable = responsable,
        cantidad = cantidad,
        observacion = observacion,
        pendiente = pendiente
    )
}
