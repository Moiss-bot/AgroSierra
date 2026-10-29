
package pe.upeu.agrosierra_ticona.presentation.laborform

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import pe.upeu.agrosierra_ticona.domain.model.TipoLabor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaborFormScreen(
    loteId: Long,
    laborId: Long? = null,
    viewModel: LaborFormViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    var menuExpandido by remember {
        mutableStateOf(false)
    }

    // ==========================================
    // INICIALIZAR FORMULARIO
    // ==========================================

    LaunchedEffect(loteId, laborId) {

        if (laborId == null) {
            viewModel.iniciarRegistro(loteId)
        } else {
            viewModel.cargarLabor(laborId)
        }
    }

    // ==========================================
    // REGRESAR DESPUÉS DE GUARDAR
    // ==========================================

    LaunchedEffect(uiState.guardadoCorrectamente) {

        if (uiState.guardadoCorrectamente) {
            viewModel.limpiarMensajes()
            onGuardado()
        }
    }

    // ==========================================
    // PANTALLA
    // ==========================================

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = if (laborId == null) {
                            "Registrar labor"
                        } else {
                            "Editar labor"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick = onVolver
                    ) {
                        Text("Volver")
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

    ) { paddingValues ->

        if (uiState.cargando) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ==================================
                // TÍTULO DEL FORMULARIO
                // ==================================

                Text(
                    text = "Datos de la labor agrícola",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // ==================================
                // TIPO DE LABOR
                // ==================================

                ExposedDropdownMenuBox(
                    expanded = menuExpandido,

                    onExpandedChange = {
                        menuExpandido = !menuExpandido
                    }
                ) {

                    OutlinedTextField(
                        value = uiState.tipo.name,

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text("Tipo de labor")
                        },

                        trailingIcon = {

                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = menuExpandido
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpandido,

                        onDismissRequest = {
                            menuExpandido = false
                        }
                    ) {

                        TipoLabor.entries.forEach { tipo ->

                            DropdownMenuItem(
                                text = {
                                    Text(tipo.name)
                                },

                                onClick = {

                                    viewModel.cambiarTipo(tipo)
                                    menuExpandido = false
                                }
                            )
                        }
                    }
                }

                // ==================================
                // FECHA
                // ==================================

                OutlinedTextField(
                    value = uiState.fecha,

                    onValueChange = viewModel::cambiarFecha,

                    label = {
                        Text("Fecha")
                    },

                    placeholder = {
                        Text("AAAA-MM-DD")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
                )

                // ==================================
                // RESPONSABLE
                // ==================================

                OutlinedTextField(
                    value = uiState.responsable,

                    onValueChange = viewModel::cambiarResponsable,

                    label = {
                        Text("Responsable")
                    },

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
                )

                // ==================================
                // CANTIDAD
                // ==================================

                OutlinedTextField(
                    value = uiState.cantidad,

                    onValueChange = viewModel::cambiarCantidad,

                    label = {
                        Text("Cantidad")
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),

                    singleLine = true,

                    modifier = Modifier.fillMaxWidth()
                )

                // ==================================
                // OBSERVACIONES
                // ==================================

                OutlinedTextField(
                    value = uiState.observacion,

                    onValueChange = viewModel::cambiarObservacion,

                    label = {
                        Text("Observaciones")
                    },

                    minLines = 3,

                    modifier = Modifier.fillMaxWidth()
                )

                // ==================================
                // MENSAJE DE ERROR
                // ==================================

                uiState.mensajeError?.let { mensaje ->

                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // ==================================
                // BOTÓN GUARDAR
                // ==================================

                Button(
                    onClick = viewModel::guardarLabor,

                    enabled = !uiState.guardando &&
                            (laborId == null || uiState.editando),

                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (uiState.guardando) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            text = if (laborId == null) {
                                "Registrar labor"
                            } else {
                                "Guardar cambios"
                            }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}
