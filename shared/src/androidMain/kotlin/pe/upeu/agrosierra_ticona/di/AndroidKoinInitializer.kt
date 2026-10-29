
package pe.upeu.agrosierra_ticona.di

import android.content.Context

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

import org.koin.dsl.module

import java.net.Proxy

fun iniciarKoinAndroid(context: Context) {

    iniciarKoin {

        modules(
            androidDatabaseModule,

            // Motor HTTP para Android
            module {
                single<HttpClientEngine> {
                    OkHttp.create {
                        config {
                            proxy(Proxy.NO_PROXY)
                        }
                    }
                }
            },

            // Contexto de Android
            module {
                single<Context> {
                    context.applicationContext
                }
            }
        )
    }
}
