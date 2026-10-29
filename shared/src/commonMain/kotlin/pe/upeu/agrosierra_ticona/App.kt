
package pe.upeu.agrosierra_ticona

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import org.koin.compose.koinInject

import pe.upeu.agrosierra_ticona.data.repository.DatosPruebaRepository
import pe.upeu.agrosierra_ticona.presentation.navigation.AgroNavigation

@Composable
fun App() {

    val datosPruebaRepository: DatosPruebaRepository =
        koinInject()

    LaunchedEffect(datosPruebaRepository) {
        datosPruebaRepository.insertarLotesSiEstaVacio()
    }

    MaterialTheme {
        AgroNavigation()
    }
}
