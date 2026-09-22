package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val ColoresClaros = lightColorScheme(
    primary = Color(0xFF006B60), onPrimary = Color.White,
    primaryContainer = Color(0xFFC9F2E8), onPrimaryContainer = Color(0xFF003D36),
    secondary = Color(0xFF52665F), secondaryContainer = Color(0xFFD5E8DF),
    tertiary = Color(0xFF936038), tertiaryContainer = Color(0xFFFFDBC0),
    background = Color(0xFFF7FAF8), surface = Color(0xFFF7FAF8),
    surfaceVariant = Color(0xFFDFE9E3), onSurface = Color(0xFF172C27),
    surfaceContainer = Color(0xFFECF3EE), surfaceContainerLow = Color(0xFFF0F6F2),
    surfaceContainerHigh = Color(0xFFE5EFE8), surfaceContainerHighest = Color(0xFFDFE9E3),
    surfaceContainerLowest = Color.White,
    onSurfaceVariant = Color(0xFF4C625B), outline = Color(0xFF71867E),
)

val ColoresOscuros = darkColorScheme(
    primary = Color(0xFF83D6C3), onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF005047), onPrimaryContainer = Color(0xFFC9F2E8),
    secondary = Color(0xFFB3CCC0), secondaryContainer = Color(0xFF354D43),
    tertiary = Color(0xFFEFB68B), tertiaryContainer = Color(0xFF613D20),
    background = Color(0xFF101C18), surface = Color(0xFF101C18),
    surfaceVariant = Color(0xFF344A41), onSurface = Color(0xFFDEEBE3),
    surfaceContainer = Color(0xFF1A2B23), surfaceContainerLow = Color(0xFF16251E),
    surfaceContainerHigh = Color(0xFF23352C), surfaceContainerHighest = Color(0xFF2D4036),
    surfaceContainerLowest = Color(0xFF0B1510),
    onSurfaceVariant = Color(0xFFB9CDC2), outline = Color(0xFF84998E),
)
