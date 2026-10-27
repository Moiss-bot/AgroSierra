
package pe.upeu.agrosierra_ticona

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import pe.upeu.agrosierra_ticona.presentation.navigation.AgroNavigation

@Composable
fun App() {

    MaterialTheme {

        // Navegación principal de AgroSierra
        AgroNavigation()

    }
}
