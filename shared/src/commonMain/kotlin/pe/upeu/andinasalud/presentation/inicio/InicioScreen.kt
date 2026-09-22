package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.usecase.InicioDatos
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun InicioScreen(estado: UiState<InicioDatos>, reintentar: () -> Unit, verCitas: () -> Unit, solicitar: () -> Unit, detalle: (String) -> Unit) {
    EstadoContenido(estado, reintentar, "No encontramos tu perfil") { datos ->
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                Text("TU SALUD, MÁS CERCA", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Text("Hola, ${datos.paciente.nombre.substringBefore(' ')}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Un espacio para cuidar de ti.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item { Text("Tu próxima cita", style = MaterialTheme.typography.titleMedium) }
            item {
                val proxima = datos.proxima
                if (proxima == null) Aviso("Aún no tienes una próxima cita programada. Puedes solicitar una cuando la necesites.")
                else CitaCard(proxima, { detalle(proxima.id) }, destacada = true)
            }
            item {
                Button(onClick = solicitar, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    Icon(Icons.Outlined.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar cita")
                }
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = verCitas, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) { Text("Mis citas") }
            }
            item { Text("Contigo en Lima Este", style = MaterialTheme.typography.titleMedium) }
            item { Text("Ñaña · Chosica · Chaclacayo · Santa Anita", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
