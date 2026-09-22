package pe.upeu.andinasalud.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import pe.upeu.andinasalud.domain.model.*

fun LocalDateTime.fechaVisible(): String {
    val meses = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
    return "${date.day} ${meses[date.month.ordinal]} ${date.year}"
}

fun EstadoCita.etiqueta(): String = when (this) {
    is EstadoCita.Programada -> "Programada"
    is EstadoCita.Atendida -> "Atendida"
    is EstadoCita.Cancelada -> "Cancelada"
}

@Composable
fun <T> EstadoContenido(
    estado: UiState<T>,
    reintentar: () -> Unit,
    tituloVacio: String = "Sin información disponible",
    descripcionVacio: String = "Vuelve a intentarlo más tarde.",
    contenido: @Composable (T) -> Unit,
) {
    when (estado) {
        UiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressIndicator()
                Text("Cargando información…", style = MaterialTheme.typography.bodyMedium)
            }
        }
        UiState.Vacio -> MensajeEstado(tituloVacio, descripcionVacio, false, reintentar)
        is UiState.Error -> MensajeEstado("Algo salió mal", estado.mensaje, true, reintentar)
        is UiState.Contenido -> contenido(estado.datos)
    }
}

@Composable
private fun MensajeEstado(titulo: String, descripcion: String, error: Boolean, reintentar: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(if (error) Icons.Outlined.ErrorOutline else Icons.Outlined.EventAvailable, null,
                Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Text(titulo, style = MaterialTheme.typography.titleLarge)
            Text(descripcion, style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = reintentar) { Text("Volver a cargar") }
        }
    }
}

@Composable
fun EtiquetaEstado(estado: EstadoCita) {
    val color = when (estado) {
        is EstadoCita.Programada -> MaterialTheme.colorScheme.primaryContainer
        is EstadoCita.Atendida -> MaterialTheme.colorScheme.secondaryContainer
        is EstadoCita.Cancelada -> MaterialTheme.colorScheme.errorContainer
    }
    val texto = when (estado) {
        is EstadoCita.Programada -> MaterialTheme.colorScheme.onPrimaryContainer
        is EstadoCita.Atendida -> MaterialTheme.colorScheme.onSecondaryContainer
        is EstadoCita.Cancelada -> MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(color = color, contentColor = texto, shape = RoundedCornerShape(8.dp)) {
        Text(estado.etiqueta(), Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium)
    }
}

/** Estado elevado: la tarjeta no conoce repositorios, ViewModels ni navegación. */
@Composable
fun CitaCard(cita: Cita, abrir: () -> Unit, destacada: Boolean = false) {
    Card(onClick = abrir, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (destacada) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                EtiquetaEstado(cita.estado)
                Text(
                    text = cita.modalidad.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(cita.medico.especialidad.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(cita.medico.nombre, style = MaterialTheme.typography.bodyLarge)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Text("${cita.fechaHora.fechaVisible()} · ${cita.fechaHora.time}", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Sede ${cita.sede.nombre}", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, "Ver detalle de la cita")
            }
        }
    }
}

@Composable
fun Dato(label: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun Aviso(mensaje: String, error: Boolean = false) {
    if (mensaje.isBlank()) return
    Surface(shape = RoundedCornerShape(12.dp), color = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (error) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer) {
        Text(mensaje, Modifier.fillMaxWidth().padding(16.dp), style = MaterialTheme.typography.bodyMedium)
    }
}
