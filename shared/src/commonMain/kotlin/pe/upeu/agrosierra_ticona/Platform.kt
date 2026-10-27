package pe.upeu.agrosierra_ticona

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform