package pe.upeu.andinasalud

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import pe.upeu.andinasalud.presentation.navigation.AppNavHost
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@Composable
fun App() {
    var oscuro by rememberSaveable { mutableStateOf(false) }
    AndinaSaludTheme(oscuro) { AppNavHost(oscuro, { oscuro = it }) }
}
