package pe.upeu.agrosierra_ticona

import android.app.Application
import pe.upeu.agrosierra_ticona.di.iniciarKoinAndroid

class AgroSierraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        iniciarKoinAndroid(this)
    }
}
