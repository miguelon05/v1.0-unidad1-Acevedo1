package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AndinaSaludTheme(oscuro: Boolean, contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (oscuro) ColoresOscuros else ColoresClaros, typography = TipografiaAndina, content = contenido)
}
