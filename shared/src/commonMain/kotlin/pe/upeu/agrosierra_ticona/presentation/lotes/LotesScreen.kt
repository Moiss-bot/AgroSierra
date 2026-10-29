
package pe.upeu.agrosierra_ticona.presentation.lotes

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

import pe.upeu.agrosierra_ticona.domain.model.Lote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotesScreen(
    viewModel: LotesViewModel,
    onLoteClick: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    // ==========================================
    // ESTADOS DE BÚSQUEDA Y FILTRO
    // ==========================================

    var textoBusqueda by remember {
        mutableStateOf("")
    }

    var valleSeleccionado by remember {
        mutableStateOf("Todos")
    }

    var menuValleExpandido by remember {
        mutableStateOf(false)
    }

    // Obtener los valles de los lotes registrados
    val vallesDisponibles = listOf("Todos") +
            uiState.lotes
                .map { it.valle }
                .distinct()
                .sorted()

    // ==========================================
    // BÚSQUEDA POR NOMBRE O CÓDIGO
    // FILTRO POR VALLE
    // ==========================================

    val lotesFiltrados = uiState.lotes.filter { lote ->

        val coincideBusqueda =
            lote.nombre.contains(
                textoBusqueda.trim(),
                ignoreCase = true
            ) ||
                    lote.codigo.contains(
                        textoBusqueda.trim(),
                        ignoreCase = true
                    )

        val coincideValle =
            valleSeleccionado == "Todos" ||
                    lote.valle == valleSeleccionado

        coincideBusqueda && coincideValle
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {

            Surface(
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically,

                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "AgroSierra - Mis lotes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = onCerrarSesion
                    ) {
                        Text("Salir")
                    }
                }
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {

            Text(
                text = "Gestión de lotes agrícolas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ==========================================
            // BUSCADOR DE LOTES
            // ==========================================

            OutlinedTextField(
                value = textoBusqueda,

                onValueChange = {
                    textoBusqueda = it
                },

                label = {
                    Text("Buscar lote")
                },

                placeholder = {
                    Text("Nombre o código")
                },

                singleLine = true,

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ==========================================
            // FILTRO POR VALLE
            // ==========================================

            ExposedDropdownMenuBox(
                expanded = menuValleExpandido,

                onExpandedChange = {
                    menuValleExpandido = it
                }
            ) {

                OutlinedTextField(
                    value = valleSeleccionado,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Filtrar por valle")
                    },

                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuValleExpandido
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
                    expanded = menuValleExpandido,

                    onDismissRequest = {
                        menuValleExpandido = false
                    }
                ) {

                    vallesDisponibles.forEach { valle ->

                        DropdownMenuItem(
                            text = {
                                Text(valle)
                            },

                            onClick = {
                                valleSeleccionado = valle
                                menuValleExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // ==========================================
            // CONTADOR DE RESULTADOS
            // ==========================================

            if (!uiState.cargando && uiState.mensajeError == null) {

                Text(
                    text = "Mostrando ${lotesFiltrados.size} " +
                            "de ${uiState.lotes.size} lotes",

                    style = MaterialTheme.typography.bodySmall,

                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            // ==========================================
            // ESTADOS DE LA PANTALLA
            // ==========================================

            when {

                // ==================================
                // CARGANDO
                // ==================================

                uiState.cargando -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ==================================
                // ERROR
                // ==================================

                uiState.mensajeError != null -> {

                    Text(
                        text = uiState.mensajeError
                            ?: "Ocurrió un error",

                        color = MaterialTheme.colorScheme.error
                    )
                }

                // ==================================
                // SIN LOTES REGISTRADOS
                // ==================================

                uiState.lotes.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No hay lotes registrados todavía.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // ==================================
                // SIN RESULTADOS DE BÚSQUEDA
                // ==================================

                lotesFiltrados.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No se encontraron lotes " +
                                    "con los criterios seleccionados.",

                            style = MaterialTheme.typography.bodyMedium,

                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ==================================
                // LISTADO DE LOTES FILTRADOS
                // ==================================

                else -> {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),

                        verticalArrangement = Arrangement.spacedBy(12.dp),

                        contentPadding = PaddingValues(
                            bottom = 16.dp
                        )
                    ) {

                        items(
                            items = lotesFiltrados,
                            key = { it.id }
                        ) { lote ->

                            LoteCard(
                                lote = lote,

                                onClick = {
                                    onLoteClick(lote.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TARJETA INDIVIDUAL DE LOTE
// ==========================================

@Composable
private fun LoteCard(
    lote: Lote,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(16.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = lote.nombre,
                style = MaterialTheme.typography.titleMedium,
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

                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Ver detalle →",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
