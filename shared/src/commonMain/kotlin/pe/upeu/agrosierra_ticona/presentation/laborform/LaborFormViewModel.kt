
package pe.upeu.agrosierra_ticona.presentation.laborform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.model.TipoLabor
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

class LaborFormViewModel(
    private val laborRepository: LaborRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LaborFormUiState()
    )

    val uiState: StateFlow<LaborFormUiState> =
        _uiState.asStateFlow()

    // ==========================================
    // 1. INICIAR REGISTRO
    // ==========================================

    fun iniciarRegistro(loteId: Long) {

        _uiState.value = LaborFormUiState(
            loteId = loteId
        )
    }

    // ==========================================
    // 2. CARGAR LABOR PARA EDITAR
    // ==========================================

    fun cargarLabor(laborId: Long) {

        viewModelScope.launch {

            _uiState.value = LaborFormUiState(
                cargando = true
            )

            try {

                val labor = laborRepository
                    .obtenerLaborPorId(laborId)

                if (labor == null) {
                    _uiState.value = LaborFormUiState(
                        mensajeError = "No se encontró la labor"
                    )
                    return@launch
                }

                _uiState.value = LaborFormUiState(
                    laborId = labor.id,
                    loteId = labor.loteId,
                    tipo = labor.tipo,
                    fecha = labor.fecha,
                    responsable = labor.responsable,
                    cantidad = labor.cantidad.toString(),
                    observacion = labor.observacion,
                    editando = true
                )

            } catch (e: Exception) {

                _uiState.value = LaborFormUiState(
                    mensajeError = e.message
                        ?: "Error al cargar la labor"
                )
            }
        }
    }

    // ==========================================
    // 3. ACTUALIZAR CAMPOS
    // ==========================================

    fun cambiarTipo(tipo: TipoLabor) {
        _uiState.value = _uiState.value.copy(
            tipo = tipo,
            mensajeError = null
        )
    }

    fun cambiarFecha(fecha: String) {
        _uiState.value = _uiState.value.copy(
            fecha = fecha,
            mensajeError = null
        )
    }

    fun cambiarResponsable(responsable: String) {
        _uiState.value = _uiState.value.copy(
            responsable = responsable,
            mensajeError = null
        )
    }

    fun cambiarCantidad(cantidad: String) {
        _uiState.value = _uiState.value.copy(
            cantidad = cantidad,
            mensajeError = null
        )
    }

    fun cambiarObservacion(observacion: String) {
        _uiState.value = _uiState.value.copy(
            observacion = observacion,
            mensajeError = null
        )
    }

    // ==========================================
    // 4. GUARDAR LABOR
    // ==========================================

    fun guardarLabor() {

        val estado = _uiState.value

        if (estado.guardando) {
            return
        }

        if (estado.loteId <= 0L) {
            _uiState.value = estado.copy(
                mensajeError = "Seleccione un lote válido"
            )
            return
        }

        val cantidadNumerica = estado.cantidad
            .replace(',', '.')
            .toDoubleOrNull()

        if (
            cantidadNumerica == null ||
            !cantidadNumerica.isFinite() ||
            cantidadNumerica <= 0.0
        ) {
            _uiState.value = estado.copy(
                mensajeError = "Ingrese una cantidad válida"
            )
            return
        }

        if (estado.fecha.isBlank()) {
            _uiState.value = estado.copy(
                mensajeError = "Ingrese la fecha"
            )
            return
        }

        if (estado.responsable.isBlank()) {
            _uiState.value = estado.copy(
                mensajeError = "Ingrese el responsable"
            )
            return
        }

        val labor = Labor(
            id = estado.laborId ?: 0L,
            loteId = estado.loteId,
            tipo = estado.tipo,
            fecha = estado.fecha.trim(),
            responsable = estado.responsable.trim(),
            cantidad = cantidadNumerica,
            observacion = estado.observacion.trim(),
            pendiente = true
        )

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null,
                guardadoCorrectamente = false
            )

            try {

                val resultado = if (estado.editando) {
                    laborRepository.actualizarLabor(labor)
                } else {
                    laborRepository.registrarLabor(labor)
                }

                resultado.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            guardando = false,
                            guardadoCorrectamente = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            guardando = false,
                            mensajeError = error.message
                                ?: "No se pudo guardar la labor"
                        )
                    }
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "Ocurrió un error al guardar"
                )
            }
        }
    }

    // ==========================================
    // 5. LIMPIAR MENSAJES
    // ==========================================

    fun limpiarMensajes() {

        _uiState.value = _uiState.value.copy(
            mensajeError = null,
            guardadoCorrectamente = false
        )
    }
}
