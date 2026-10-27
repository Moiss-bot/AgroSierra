
package pe.upeu.agrosierra_ticona.presentation.detallelote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

class DetalleLoteViewModel(
    private val loteRepository: LoteRepository,
    private val laborRepository: LaborRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DetalleLoteUiState()
    )

    val uiState: StateFlow<DetalleLoteUiState> =
        _uiState.asStateFlow()

    private var loteJob: Job? = null
    private var laboresJob: Job? = null

    // ==========================================
    // 1. CARGAR DETALLE DEL LOTE
    // ==========================================

    fun cargarLote(loteId: Long) {

        loteJob?.cancel()
        laboresJob?.cancel()

        _uiState.value = DetalleLoteUiState(
            cargando = true
        )

        loteJob = viewModelScope.launch {

            loteRepository
                .observarLotePorId(loteId)
                .catch { error ->

                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        mensajeError = error.message
                            ?: "Error al cargar el lote"
                    )
                }
                .collect { lote ->

                    _uiState.value = _uiState.value.copy(
                        lote = lote,
                        cargando = false
                    )
                }
        }

        laboresJob = viewModelScope.launch {

            laborRepository
                .observarLaboresPorLote(loteId)
                .catch { error ->

                    _uiState.value = _uiState.value.copy(
                        mensajeError = error.message
                            ?: "Error al cargar las labores"
                    )
                }
                .collect { labores ->

                    _uiState.value = _uiState.value.copy(
                        labores = labores
                    )
                }
        }
    }

    // ==========================================
    // 2. CERRAR CAMPAÑA
    // ==========================================

    fun cerrarCampania() {

        val lote = _uiState.value.lote ?: return

        if (lote.campaniaCerrada) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "La campaña ya está cerrada"
            )
            return
        }

        if (_uiState.value.procesando) {
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                procesando = true,
                mensajeError = null,
                mensajeExito = null
            )

            val resultado = try {
                loteRepository.cerrarCampania(lote.id)
            } catch (e: Exception) {
                Result.failure(e)
            }

            resultado.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        procesando = false,
                        mensajeExito = "Campaña cerrada correctamente"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        procesando = false,
                        mensajeError = error.message
                            ?: "No se pudo cerrar la campaña"
                    )
                }
            )
        }
    }

    // ==========================================
    // 3. LIMPIAR MENSAJES
    // ==========================================

    fun limpiarMensajes() {

        _uiState.value = _uiState.value.copy(
            mensajeError = null,
            mensajeExito = null
        )
    }
}
