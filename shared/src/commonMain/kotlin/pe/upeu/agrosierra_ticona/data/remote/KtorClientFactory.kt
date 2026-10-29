
package pe.upeu.agrosierra_ticona.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClientFactory {

    fun crearCliente(
        engine: HttpClientEngine
    ): HttpClient {
        println("AgroAPI MOTOR HTTP: ${engine::class.simpleName}")

        return HttpClient(engine) {

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 30000L
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("AgroAPI: $message")
                    }
                }
                level = LogLevel.INFO
            }

            expectSuccess = false
        }
    }
}
