
package pe.upeu.agrosierra_ticona.di

import android.content.Context
import org.koin.dsl.module

fun iniciarKoinAndroid(context: Context) {
    iniciarKoin {
        modules(
            androidDatabaseModule,
            module {
                single<Context> {
                    context.applicationContext
                }
            }
        )
    }
}
