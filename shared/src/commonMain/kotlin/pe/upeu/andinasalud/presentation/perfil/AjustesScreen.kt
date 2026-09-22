package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AjustesScreen(oscuro: Boolean, cambiarTema: (Boolean) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            Text("A tu manera", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Elige cómo prefieres ver AndinaSalud.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Tema oscuro", style = MaterialTheme.typography.titleMedium)
                        Text(if (oscuro) "Activado" else "Desactivado", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(checked = oscuro, onCheckedChange = cambiarTema)
                }
            }
        }
        item { Text("El cambio se aplica de inmediato a todas las pantallas.", style = MaterialTheme.typography.bodyMedium) }
    }
}
