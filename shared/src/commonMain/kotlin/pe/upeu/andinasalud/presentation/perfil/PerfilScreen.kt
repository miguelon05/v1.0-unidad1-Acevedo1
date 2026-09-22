package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun PerfilScreen(estado: UiState<Paciente>, reintentar: () -> Unit, ajustes: () -> Unit) {
    EstadoContenido(estado, reintentar, "Perfil no disponible") { paciente ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Outlined.Person, null, Modifier.padding(18.dp).size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.height(16.dp))
                Text(paciente.nombre, style = MaterialTheme.typography.headlineMedium)
                Text("Paciente AndinaSalud", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item { Dato("Documento de identidad", paciente.documento) }
            item { Dato("Correo electrónico", paciente.correo) }
            item { Dato("Teléfono", paciente.telefono) }
            item { HorizontalDivider() }
            item { FilledTonalButton(onClick = ajustes, modifier = Modifier.fillMaxWidth()) { Text("Ajustes de apariencia") } }
        }
    }
}
