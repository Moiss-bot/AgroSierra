
package pe.upeu.agrosierra_ticona.presentation.detallelote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

data class EliminarLaborUiState(
    val eliminando: Boolean = false,
    val mensajeError: String? = null,
    val eliminadaCorrectamente: Boolean = false
)

class EliminarLaborViewModel(
    private val laborRepository: LaborRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EliminarLaborUiState()
    )

    val uiState: StateFlow<EliminarLaborUiState> =
        _uiState.asStateFlow()

    fun eliminarLabor(laborId: Long) {

        if (_uiState.value.eliminando) {
            return
        }

        viewModelScope.launch {

            _uiState.value = EliminarLaborUiState(
                eliminando = true
            )

            try {

                val resultado = laborRepository
                    .eliminarLabor(laborId)

                resultado.fold(
                    onSuccess = {
                        _uiState.value = EliminarLaborUiState(
                            eliminadaCorrectamente = true
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = EliminarLaborUiState(
                            mensajeError = error.message
                                ?: "No se pudo eliminar la labor"
                        )
                    }
                )

            } catch (e: Exception) {

                _uiState.value = EliminarLaborUiState(
                    mensajeError = e.message
                        ?: "Error al eliminar la labor"
                )
            }
        }
    }

    fun limpiarEstado() {
        _uiState.value = EliminarLaborUiState()
    }
}
