package pe.upeu.andinasalud

import android.app.Application
import org.koin.android.ext.koin.androidContext
import pe.upeu.andinasalud.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@MainApplication) }
    }
}
