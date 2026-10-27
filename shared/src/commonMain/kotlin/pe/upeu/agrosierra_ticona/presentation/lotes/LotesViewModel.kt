
package pe.upeu.agrosierra_ticona.presentation.lotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository

class LotesViewModel(
    private val loteRepository: LoteRepository
) : ViewModel() {

    // Estado privado y modificable
    private val _uiState = MutableStateFlow(
        LotesUiState()
    )

    // Estado público para la interfaz
    val uiState: StateFlow<LotesUiState> =
        _uiState.asStateFlow()

    init {
        observarLotes()
    }

    // ==========================================
    // 1. OBSERVAR LOTES DESDE SQLITE
    // ==========================================

    private fun observarLotes() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensajeError = null
            )

            loteRepository
                .observarLotes()
                .catch { error ->

                    _uiState.value = _uiState.value.copy(
                        cargando = false,
                        mensajeError = error.message
                            ?: "Error al cargar los lotes"
                    )
                }
                .collect { lotes ->

                    _uiState.value = _uiState.value.copy(
                        lotes = lotes,
                        cargando = false
                    )
                }
        }
    }

    // ==========================================
    // 2. SINCRONIZAR LOTES
    // ==========================================

    fun sincronizarLotes() {

        if (_uiState.value.sincronizando) {
            return
        }

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                sincronizando = true,
                mensajeError = null,
                mensajeExito = null
            )

            try {

                loteRepository.sincronizarLotes()

                _uiState.value = _uiState.value.copy(
                    sincronizando = false,
                    mensajeExito = "Sincronización finalizada"
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    sincronizando = false,
                    mensajeError = e.message
                        ?: "No se pudieron sincronizar los lotes"
                )
            }
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
