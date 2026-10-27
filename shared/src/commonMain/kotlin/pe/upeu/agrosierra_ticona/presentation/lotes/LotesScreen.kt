
package pe.upeu.agrosierra_ticona.presentation.lotes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import pe.upeu.agrosierra_ticona.domain.model.Lote

@Composable
fun LotesScreen(
    viewModel: LotesViewModel,
    onLoteClick: (Long) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "AgroSierra - Mis lotes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Gestión de lotes agrícolas",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {

                uiState.cargando -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.mensajeError != null -> {

                    Text(
                        text = uiState.mensajeError
                            ?: "Ocurrió un error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

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

                else -> {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            bottom = 16.dp
                        )
                    ) {

                        items(
                            items = uiState.lotes,
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

            Text(
                text = "Código: ${lote.codigo}"
            )

            Text(
                text = "Cultivo: ${lote.cultivo}"
            )

            Text(
                text = "Valle: ${lote.valle}"
            )

            Text(
                text = "Superficie: ${lote.hectareas} ha"
            )

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
