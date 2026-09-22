package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.DetalleDatos
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun DetalleCitaScreen(estado: UiState<DetalleDatos>, accion: CancelacionUiState, reintentar: () -> Unit, cancelar: () -> Unit) {
    var confirmar by rememberSaveable { mutableStateOf(false) }
    EstadoContenido(estado, reintentar, "Cita no encontrada", "Regresa a Mis citas y selecciona otra cita.") { datos ->
        val cita = datos.cita
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item { EtiquetaEstado(cita.estado) }
            item { Text(cita.medico.especialidad.nombre, style = MaterialTheme.typography.headlineMedium) }
            item { Dato("Profesional", cita.medico.nombre) }
            item { Dato("Sede", cita.sede.nombre) }
            item { Dato("Fecha y hora", "${cita.fechaHora.fechaVisible()} · ${cita.fechaHora.time}") }
            item { HorizontalDivider() }
            item { Dato("Motivo de consulta", cita.motivo) }
            item {
                Dato("Indicaciones", when (val estadoCita = cita.estado) {
                    is EstadoCita.Atendida -> estadoCita.indicaciones
                    else -> cita.indicaciones
                })
            }
            item {
                when (val estadoCita = cita.estado) {
                    is EstadoCita.Programada -> Dato("Recordatorio", if (estadoCita.recordatorioActivo) "Activo" else "Inactivo")
                    is EstadoCita.Cancelada -> {
                        Dato("Motivo de cancelación", estadoCita.motivo)
                        Spacer(Modifier.height(8.dp))
                        Dato("Cancelada por", if (estadoCita.canceladaPorPaciente) "El paciente" else "El centro médico")
                    }
                    is EstadoCita.Atendida -> Unit
                }
            }
            item { Aviso(accion.mensaje) }
            item {
                datos.impedimentoCancelacion?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { confirmar = true }, enabled = datos.impedimentoCancelacion == null && !accion.procesando,
                    modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text(if (accion.procesando) "Cancelando…" else "Cancelar cita")
                }
            }
        }
    }
    if (confirmar) AlertDialog(onDismissRequest = { confirmar = false },
        title = { Text("¿Cancelar esta cita?") }, text = { Text("La cita dejará de estar programada. Si necesitas atención, podrás solicitar una nueva.") },
        confirmButton = { TextButton(onClick = { confirmar = false; cancelar() }) { Text("Sí, cancelar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Conservar cita") } })
}
