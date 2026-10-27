
package pe.upeu.agrosierra_ticona.presentation.detallelote

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import pe.upeu.agrosierra_ticona.domain.model.Labor
import pe.upeu.agrosierra_ticona.domain.model.Lote

@Composable
fun DetalleLoteScreen(
    loteId: Long,
    viewModel: DetalleLoteViewModel,
    onVolver: () -> Unit,
    onRegistrarLabor: (Long) -> Unit,
    onEditarLabor: (Long) -> Unit,
    onEliminarLabor: (Long) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(loteId) {
        viewModel.cargarLote(loteId)
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Detalle del lote",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->

        when {
            uiState.cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.lote == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.mensajeError
                            ?: "No se encontró el lote"
                    )
                }
            }

            else -> {
                val lote = uiState.lote!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {
                        InformacionLoteCard(lote)
                    }

                    item {
                        Text(
                            text = "Labores agrícolas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!lote.campaniaCerrada) {
                        item {
                            Button(
                                onClick = {
                                    onRegistrarLabor(lote.id)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Registrar nueva labor")
                            }
                        }
                    }

                    if (uiState.labores.isEmpty()) {
                        item {
                            Text(
                                text = "Este lote todavía no tiene labores registradas.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(
                            items = uiState.labores,
                            key = { it.id }
                        ) { labor ->
                            LaborCard(
                                labor = labor,
                                permitirCambios = !lote.campaniaCerrada,
                                onEditar = {
                                    onEditarLabor(labor.id)
                                },
                                onEliminar = {
                                    onEliminarLabor(labor.id)
                                }
                            )
                        }
                    }

                    uiState.mensajeError?.let { mensaje ->
                        item {
                            Text(
                                text = mensaje,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    uiState.mensajeExito?.let { mensaje ->
                        item {
                            Text(
                                text = mensaje,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// INFORMACIÓN DEL LOTE
// ==========================================

@Composable
private fun InformacionLoteCard(
    lote: Lote
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = lote.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text("Código: ${lote.codigo}")
            Text("Cultivo: ${lote.cultivo}")
            Text("Valle: ${lote.valle}")
            Text("Superficie: ${lote.hectareas} ha")

            HorizontalDivider()

            Text(
                text = if (lote.campaniaCerrada) {
                    "Campaña cerrada"
                } else {
                    "Campaña abierta"
                },
                color = if (lote.campaniaCerrada) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// TARJETA DE LABOR AGRÍCOLA
// ==========================================

@Composable
private fun LaborCard(
    labor: Labor,
    permitirCambios: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = labor.tipo.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Fecha: ${labor.fecha}")
            Text("Responsable: ${labor.responsable}")
            Text("Cantidad: ${labor.cantidad}")
            Text("Observación: ${labor.observacion}")

            if (labor.pendiente) {
                Text(
                    text = "Pendiente de sincronización",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            if (permitirCambios) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditar
                    ) {
                        Text("Editar")
                    }

                    OutlinedButton(
                        onClick = onEliminar,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
