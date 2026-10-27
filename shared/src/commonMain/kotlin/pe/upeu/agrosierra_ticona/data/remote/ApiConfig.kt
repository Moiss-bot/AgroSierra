
package pe.upeu.agrosierra_ticona.data.remote

object ApiConfig {

    // Dirección provisional para el emulador Android.
    // 10.0.2.2 representa la computadora anfitriona.
    // Cambia el puerto según la configuración de AgroAPI.
    const val BASE_URL = "http://10.0.2.2:8080"

    const val API_VERSION = "/api/v1"

    const val LOGIN = "$API_VERSION/auth/login"

    const val LOTES = "$API_VERSION/lotes"

    const val LABORES = "$API_VERSION/labores"

    const val USUARIO_ACTUAL = "$API_VERSION/usuarios/me"

    fun lotePorId(id: Long): String {
        return "$LOTES/$id"
    }

    fun laboresPorLote(id: Long): String {
        return "$LOTES/$id/labores"
    }

    fun laborPorId(id: Long): String {
        return "$LABORES/$id"
    }

    fun cerrarCampania(id: Long): String {
        return "$LOTES/$id/cerrar-campania"
    }
}
