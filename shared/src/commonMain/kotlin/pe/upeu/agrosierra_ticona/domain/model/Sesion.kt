
package pe.upeu.agrosierra_ticona.domain.model

data class Sesion(
    val token: String,
    val usuario: String,
    val rol: String,
    val privilegios: Set<String>,

    // Duración de la sesión en segundos
    val expiraEnSegundos: Long = 3600L,

    // Momento de expiración en milisegundos
    val fechaExpiracion: Long = 0L
) {
    fun estaExpirada(ahoraMillis: Long): Boolean {
        return fechaExpiracion <= ahoraMillis
    }
}
