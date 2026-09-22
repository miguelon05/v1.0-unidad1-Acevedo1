package pe.upeu.andinasalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.android.ext.android.get
import pe.upeu.andinasalud.data.local.ControlSimulacion

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (BuildConfig.DEBUG) {
            // Solo para la verificación reproducible de carga, error y vacío con adb.
            get<ControlSimulacion>().configurar(intent.getStringExtra("escenario") ?: "normal")
        }
        setContent { App() }
    }
}
