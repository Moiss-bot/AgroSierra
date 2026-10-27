
package pe.upeu.agrosierra_ticona.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun iniciarKoin(
    configuracion: KoinAppDeclaration = {}
) {
    startKoin {
        configuracion()
        modules(appModule)
    }
}
