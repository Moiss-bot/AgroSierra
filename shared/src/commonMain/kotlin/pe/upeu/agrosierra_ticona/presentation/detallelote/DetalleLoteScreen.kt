
package pe.upeu.agrosierra_ticona.presentation.detallelote

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

// ==========================================
// TIPOS DE FILTRO
// ==========================================

private enum class FiltroLabor {
    TODAS,
    PENDIENTES,
    NO_PENDIENTES
}

// ==========================================
// PANTALLA DETALLE DEL LOTE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
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

    var filtroSeleccionado by remember(loteId) {
        mutableStateOf(FiltroLabor.TODAS)
    }

    LaunchedEffect(loteId) {
        viewModel.cargarLote(loteId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del lote",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

    ) { paddingValues ->

        when {

            // ==================================
            // CARGANDO
            // ==================================

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

            // ==================================
            // LOTE NO ENCONTRADO
            // ==================================

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

            // ==================================
            // CONTENIDO PRINCIPAL
            // ==================================

            else -> {

                val lote = uiState.lote!!

                val totalLabores = uiState.labores.size

                val laboresPendientes = uiState.labores.count {
                    it.pendiente
                }

                // Filtramos solo los datos que se muestran.
                // No modificamos SQLDelight.

                val laboresFiltradas = when (filtroSeleccionado) {

                    FiltroLabor.TODAS -> {
                        uiState.labores
                    }

                    FiltroLabor.PENDIENTES -> {
                        uiState.labores.filter {
                            it.pendiente
                        }
                    }

                    FiltroLabor.NO_PENDIENTES -> {
                        uiState.labores.filter {
                            !it.pendiente
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentPadding = PaddingValues(16.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ==================================
                    // INFORMACIÓN GENERAL DEL LOTE
                    // ==================================

                    item {
                        InformacionLoteCard(
                            lote = lote
                        )
                    }

                    // ==================================
                    // RESUMEN DE SINCRONIZACIÓN
                    // ==================================

                    item {
                        ResumenSincronizacionCard(
                            totalLabores = totalLabores,
                            laboresPendientes = laboresPendientes
                        )
                    }

                    // ==================================
                    // TÍTULO DE LABORES
                    // ==================================

                    item {
                        Text(
                            text = "Labores agrícolas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // ==================================
                    // REGISTRAR LABOR
                    // ==================================

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

                    // ==================================
                    // FILTROS
                    // ==================================

                    item {
                        FiltrosLabores(
                            filtroSeleccionado = filtroSeleccionado,

                            onFiltroSeleccionado = { nuevoFiltro ->
                                filtroSeleccionado = nuevoFiltro
                            }
                        )
                    }

                    // ==================================
                    // CONTADOR DE RESULTADOS
                    // ==================================

                    item {
                        Text(
                            text = "Mostrando ${laboresFiltradas.size} " +
                                    "de $totalLabores labores",

                            style = MaterialTheme.typography.bodySmall,

                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // ==================================
                    // LISTADO FILTRADO
                    // ==================================

                    if (laboresFiltradas.isEmpty()) {

                        item {
                            Text(
                                text = if (totalLabores == 0) {
                                    "Este lote todavía no tiene " +
                                            "labores registradas."
                                } else {
                                    "No hay labores que coincidan " +
                                            "con el filtro seleccionado."
                                },

                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    } else {

                        items(
                            items = laboresFiltradas,
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

                    // ==================================
                    // MENSAJES DE ERROR
                    // ==================================

                    uiState.mensajeError?.let { mensaje ->

                        item {
                            Text(
                                text = mensaje,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // ==================================
                    // MENSAJES DE ÉXITO
                    // ==================================

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
// RESUMEN DE SINCRONIZACIÓN
// ==========================================

@Composable
private fun ResumenSincronizacionCard(
    totalLabores: Int,
    laboresPendientes: Int
) {

    val laboresNoPendientes =
        totalLabores - laboresPendientes

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Estado de sincronización",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            Text("Total de labores: $totalLabores")

            Text(
                text = "Pendientes: $laboresPendientes",

                color = if (laboresPendientes > 0) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.primary
                },

                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "No pendientes: $laboresNoPendientes"
            )

            HorizontalDivider()

            if (laboresPendientes > 0) {

                Text(
                    text = "Las labores pendientes están " +
                            "guardadas localmente en SQLDelight. " +
                            "Todavía no se envían a AgroAPI.",

                    style = MaterialTheme.typography.bodySmall,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            } else {

                Text(
                    text = "No hay labores marcadas como " +
                            "pendientes de sincronización.",

                    style = MaterialTheme.typography.bodySmall,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ==========================================
// FILTROS DE LABORES
// ==========================================

@Composable
private fun FiltrosLabores(
    filtroSeleccionado: FiltroLabor,
    onFiltroSeleccionado: (FiltroLabor) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "Filtrar labores",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        // FlowRow permite que los botones pasen
        // a otra línea en pantallas pequeñas.

        FlowRow(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.spacedBy(8.dp),

            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            FilterChip(
                selected = filtroSeleccionado == FiltroLabor.TODAS,

                onClick = {
                    onFiltroSeleccionado(FiltroLabor.TODAS)
                },

                label = {
                    Text("Todas")
                }
            )

            FilterChip(
                selected = filtroSeleccionado == FiltroLabor.PENDIENTES,

                onClick = {
                    onFiltroSeleccionado(FiltroLabor.PENDIENTES)
                },

                label = {
                    Text("Pendientes")
                }
            )

            FilterChip(
                selected = filtroSeleccionado == FiltroLabor.NO_PENDIENTES,

                onClick = {
                    onFiltroSeleccionado(FiltroLabor.NO_PENDIENTES)
                },

                label = {
                    Text("No pendientes")
                }
            )
        }
    }
}

// ==========================================
// FORMATO DE CANTIDAD
// ==========================================

private fun formatearCantidad(
    cantidad: Double
): String {

    return if (
        cantidad.isFinite() &&
        cantidad % 1.0 == 0.0 &&
        cantidad >= Long.MIN_VALUE.toDouble() &&
        cantidad < 9223372036854775808.0
    ) {
        cantidad.toLong().toString()
    } else {
        cantidad.toString()
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

            Text(
                text = "Cantidad: ${formatearCantidad(labor.cantidad)}"
            )

            Text("Observación: ${labor.observacion}")

            // ==================================
            // ESTADO DE SINCRONIZACIÓN
            // ==================================

            if (labor.pendiente) {

                Text(
                    text = "Pendiente de sincronización",

                    color = MaterialTheme.colorScheme.tertiary,

                    style = MaterialTheme.typography.labelMedium,

                    fontWeight = FontWeight.SemiBold
                )

            } else {

                Text(
                    text = "Sin sincronización pendiente",

                    color = MaterialTheme.colorScheme.primary,

                    style = MaterialTheme.typography.labelMedium
                )
            }

            // ==================================
            // EDITAR Y ELIMINAR
            // ==================================

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
