
package pe.upeu.agrosierra_ticona.presentation.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*

import org.koin.compose.koinInject
import pe.upeu.agrosierra_ticona.domain.repository.LoteRepository
import pe.upeu.agrosierra_ticona.domain.repository.LaborRepository

import pe.upeu.agrosierra_ticona.presentation.lotes.LotesScreen
import pe.upeu.agrosierra_ticona.presentation.lotes.LotesViewModel

import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteScreen
import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteViewModel
import pe.upeu.agrosierra_ticona.presentation.detallelote.EliminarLaborViewModel

import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormScreen
import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormViewModel

private sealed interface Pantalla {

    data object Lotes : Pantalla

    data class DetalleLote(
        val loteId: Long
    ) : Pantalla

    data class FormularioLabor(
        val loteId: Long,
        val laborId: Long? = null
    ) : Pantalla
}

@Composable
fun AgroNavigation() {

    // ==========================================
    // DEPENDENCIAS DE KOIN
    // ==========================================

    val loteRepository: LoteRepository = koinInject()
    val laborRepository: LaborRepository = koinInject()

    // ViewModels conservados durante recomposición.
    // Más adelante implementaremos propietarios
    // de ciclo de vida por pantalla.

    val lotesViewModel = remember(
        loteRepository
    ) {
        LotesViewModel(
            loteRepository = loteRepository
        )
    }

    val detalleViewModel = remember(
        loteRepository,
        laborRepository
    ) {
        DetalleLoteViewModel(
            loteRepository = loteRepository,
            laborRepository = laborRepository
        )
    }

    val laborFormViewModel = remember(
        laborRepository
    ) {
        LaborFormViewModel(
            laborRepository = laborRepository
        )
    }

    val eliminarLaborViewModel = remember(
        laborRepository
    ) {
        EliminarLaborViewModel(
            laborRepository = laborRepository
        )
    }

    val eliminarUiState by eliminarLaborViewModel
        .uiState.collectAsState()

    var pantallaActual by remember {
        mutableStateOf<Pantalla>(Pantalla.Lotes)
    }

    var laborParaEliminar by remember {
        mutableStateOf<Long?>(null)
    }

    // ==========================================
    // PANTALLA PRINCIPAL
    // ==========================================

    when (val pantalla = pantallaActual) {

        Pantalla.Lotes -> {

            LotesScreen(
                viewModel = lotesViewModel,
                onLoteClick = { loteId ->

                    pantallaActual = Pantalla.DetalleLote(
                        loteId = loteId
                    )
                }
            )
        }

        // ======================================
        // DETALLE DEL LOTE
        // ======================================

        is Pantalla.DetalleLote -> {

            DetalleLoteScreen(
                loteId = pantalla.loteId,
                viewModel = detalleViewModel,

                onVolver = {
                    pantallaActual = Pantalla.Lotes
                },

                onRegistrarLabor = { loteId ->

                    pantallaActual = Pantalla.FormularioLabor(
                        loteId = loteId
                    )
                },

                onEditarLabor = { laborId ->

                    pantallaActual = Pantalla.FormularioLabor(
                        loteId = pantalla.loteId,
                        laborId = laborId
                    )
                },

                onEliminarLabor = { laborId ->

                    eliminarLaborViewModel.limpiarEstado()
                    laborParaEliminar = laborId
                }
            )
        }

        // ======================================
        // FORMULARIO DE LABORES
        // ======================================

        is Pantalla.FormularioLabor -> {

            LaborFormScreen(
                loteId = pantalla.loteId,
                laborId = pantalla.laborId,
                viewModel = laborFormViewModel,

                onVolver = {

                    pantallaActual = Pantalla.DetalleLote(
                        loteId = pantalla.loteId
                    )
                },

                onGuardado = {

                    pantallaActual = Pantalla.DetalleLote(
                        loteId = pantalla.loteId
                    )
                }
            )
        }
    }

    // ==========================================
    // CONFIRMAR ELIMINACIÓN
    // ==========================================

    laborParaEliminar?.let { laborId ->

        AlertDialog(
            onDismissRequest = {

                if (!eliminarUiState.eliminando) {
                    laborParaEliminar = null
                    eliminarLaborViewModel.limpiarEstado()
                }
            },

            title = {
                Text("Eliminar labor")
            },

            text = {

                if (eliminarUiState.eliminando) {

                    CircularProgressIndicator()

                } else {

                    Text(
                        eliminarUiState.mensajeError
                            ?: "¿Estás seguro de eliminar esta labor agrícola?"
                    )
                }
            },

            confirmButton = {

                TextButton(
                    enabled = !eliminarUiState.eliminando,
                    onClick = {

                        eliminarLaborViewModel.eliminarLabor(
                            laborId
                        )
                    }
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {

                TextButton(
                    enabled = !eliminarUiState.eliminando,
                    onClick = {

                        laborParaEliminar = null
                        eliminarLaborViewModel.limpiarEstado()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // ==========================================
    // FINALIZAR ELIMINACIÓN
    // ==========================================

    LaunchedEffect(
        eliminarUiState.eliminadaCorrectamente
    ) {

        if (eliminarUiState.eliminadaCorrectamente) {

            laborParaEliminar = null
            eliminarLaborViewModel.limpiarEstado()
        }
    }
}
