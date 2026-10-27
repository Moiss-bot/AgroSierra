
package pe.upeu.agrosierra_ticona.presentation.navigation

import androidx.compose.runtime.*
import org.koin.core.context.GlobalContext

import pe.upeu.agrosierra_ticona.presentation.lotes.LotesScreen
import pe.upeu.agrosierra_ticona.presentation.lotes.LotesViewModel

import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteScreen
import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteViewModel

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

    val koin = remember {
        GlobalContext.get()
    }

    val lotesViewModel = remember {
        koin.get<LotesViewModel>()
    }

    val detalleViewModel = remember {
        koin.get<DetalleLoteViewModel>()
    }

    val laborFormViewModel = remember {
        koin.get<LaborFormViewModel>()
    }

    var pantallaActual by remember {
        mutableStateOf<Pantalla>(Pantalla.Lotes)
    }

    when (val pantalla = pantallaActual) {

        // =====================================
        // PANTALLA DE LOTES
        // =====================================

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

        // =====================================
        // DETALLE DEL LOTE
        // =====================================

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

                    // Implementaremos el diálogo
                    // de confirmación de eliminación
                    // en el siguiente paso.
                }
            )
        }

        // =====================================
        // FORMULARIO DE LABOR
        // =====================================

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
}
