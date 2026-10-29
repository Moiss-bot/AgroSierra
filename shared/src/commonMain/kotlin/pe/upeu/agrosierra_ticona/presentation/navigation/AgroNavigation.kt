
package pe.upeu.agrosierra_ticona.presentation.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.*

import org.koin.compose.viewmodel.koinViewModel

import pe.upeu.agrosierra_ticona.presentation.login.LoginScreen
import pe.upeu.agrosierra_ticona.presentation.login.LoginViewModel

import pe.upeu.agrosierra_ticona.presentation.lotes.LotesScreen
import pe.upeu.agrosierra_ticona.presentation.lotes.LotesViewModel

import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteScreen
import pe.upeu.agrosierra_ticona.presentation.detallelote.DetalleLoteViewModel
import pe.upeu.agrosierra_ticona.presentation.detallelote.EliminarLaborViewModel

import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormScreen
import pe.upeu.agrosierra_ticona.presentation.laborform.LaborFormViewModel

// ==========================================
// PANTALLAS DE LA APLICACIÓN
// ==========================================

private sealed interface Pantalla {

    data object Login : Pantalla

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
    // 1. VIEWMODELS DE KOIN
    // ==========================================

    val loginViewModel: LoginViewModel = koinViewModel()

    val lotesViewModel: LotesViewModel = koinViewModel()

    val detalleViewModel: DetalleLoteViewModel = koinViewModel()

    val laborFormViewModel: LaborFormViewModel = koinViewModel()

    val eliminarLaborViewModel: EliminarLaborViewModel = koinViewModel()

    // ==========================================
    // 2. ESTADO DE ELIMINACIÓN
    // ==========================================

    val eliminarUiState by eliminarLaborViewModel
        .uiState.collectAsState()

    var laborParaEliminar by remember {
        mutableStateOf<Long?>(null)
    }

    // ==========================================
    // 3. ESTADO DE NAVEGACIÓN
    // ==========================================

    var pantallaActual by remember {
        mutableStateOf<Pantalla>(Pantalla.Login)
    }

    // ==========================================
    // 4. NAVEGACIÓN PRINCIPAL
    // ==========================================

    when (val pantalla = pantallaActual) {

        // ======================================
        // LOGIN
        // ======================================

        Pantalla.Login -> {

            LoginScreen(
                viewModel = loginViewModel,

                // NUEVO: Navegar después del login JWT
                onLoginExitoso = {
                    pantallaActual = Pantalla.Lotes
                },

                // Acceso temporal para pruebas
                onIngresarModoDesarrollo = {
                    pantallaActual = Pantalla.Lotes
                }
            )
        }

        // ======================================
        // LISTADO DE LOTES
        // ======================================

        Pantalla.Lotes -> {

            LotesScreen(
                viewModel = lotesViewModel,

                onLoteClick = { loteId ->
                    pantallaActual = Pantalla.DetalleLote(
                        loteId = loteId
                    )
                },

                onCerrarSesion = {
                    laborParaEliminar = null

                    eliminarLaborViewModel.limpiarEstado()

                    pantallaActual = Pantalla.Login
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
    // 5. CONFIRMACIÓN DE ELIMINACIÓN
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
    // 6. FINALIZAR ELIMINACIÓN
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
