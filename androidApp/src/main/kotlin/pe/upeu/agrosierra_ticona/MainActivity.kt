
package pe.upeu.agrosierra_ticona

import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Prueba temporal de conexión con Spring Boot
        probarConexionBackend()

        // Interfaz original de AgroSierra
        setContent {
            App()
        }
    }

    private fun probarConexionBackend() {

        Thread {
            var conexion: HttpURLConnection? = null

            try {
                val url = URL(
                    "http://10.0.2.2:8080/api/v1/lotes"
                )

                conexion = url.openConnection()
                        as HttpURLConnection

                conexion.connectTimeout = 5000
                conexion.readTimeout = 5000
                conexion.requestMethod = "GET"
                conexion.instanceFollowRedirects = false

                val codigo = conexion.responseCode

                Log.i(
                    "AgroConexion",
                    "Respuesta HTTP del backend: $codigo"
                )

            } catch (e: Exception) {

                Log.e(
                    "AgroConexion",
                    "Error de conexión: ${e.message}",
                    e
                )

            } finally {
                conexion?.disconnect()
            }

        }.start()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
