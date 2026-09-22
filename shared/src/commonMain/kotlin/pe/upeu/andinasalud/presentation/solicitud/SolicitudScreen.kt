package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun SolicitudScreen(estado: UiState<Catalogo>, formulario: FormularioUiState, editar: (CampoSolicitud, String) -> Unit, reintentar: () -> Unit, enviar: () -> Unit) {
    EstadoContenido(estado, reintentar, "Sin horarios disponibles", "No hay un catálogo de atención disponible en este momento.") { catalogo ->
        val solicitud = formulario.solicitud
        LazyColumn(Modifier.fillMaxSize().imePadding(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            item {
                Text("Agenda tu atención", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text("Elige una especialidad y una sede. Te asignaremos un profesional de esa especialidad en la sede elegida.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Selector("Especialidad", catalogo.especialidades.map { it.id to it.nombre }, solicitud.especialidadId,
                    formulario.errores[CampoSolicitud.ESPECIALIDAD], !formulario.enviando) { editar(CampoSolicitud.ESPECIALIDAD, it) }
            }
            item {
                Selector("Sede", catalogo.sedes.map { it.id to it.nombre }, solicitud.sedeId,
                    formulario.errores[CampoSolicitud.SEDE], !formulario.enviando) { editar(CampoSolicitud.SEDE, it) }
            }
            item {
                Selector(
                    "Modalidad", 
                    Modalidad.entries.map { it.name to it.name }, 
                    solicitud.modalidad.name,
                    null, 
                    !formulario.enviando
                ) { modalStr ->
                    editar(CampoSolicitud.MODALIDAD, modalStr)
                }
            }
            item {
                Campo("Fecha (AAAA-MM-DD)", solicitud.fecha, formulario.errores[CampoSolicitud.FECHA], !formulario.enviando,
                    "Ejemplo: 2026-10-15") { editar(CampoSolicitud.FECHA, it) }
            }
            item {
                Campo("Hora (HH:MM)", solicitud.hora, formulario.errores[CampoSolicitud.HORA], !formulario.enviando,
                    "Formato de 24 horas. Atención en horario de Lima.") { editar(CampoSolicitud.HORA, it) }
            }
            item {
                OutlinedTextField(value = solicitud.motivo, onValueChange = { editar(CampoSolicitud.MOTIVO, it) },
                    label = { Text("Motivo de consulta") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 6,
                    enabled = !formulario.enviando, isError = CampoSolicitud.MOTIVO in formulario.errores,
                    supportingText = { Text(formulario.errores[CampoSolicitud.MOTIVO] ?: "${solicitud.motivo.trim().length}/200 caracteres · mínimo 10") })
            }
            item { Aviso(formulario.mensaje, error = true) }
            item {
                Button(onClick = enviar, enabled = !formulario.enviando, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    Text(if (formulario.enviando) "Solicitando…" else "Confirmar solicitud")
                }
            }
        }
    }
}

@Composable
private fun Selector(label: String, opciones: List<Pair<String, String>>, valor: String, error: String?, habilitado: Boolean, seleccionar: (String) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expandido = true }, enabled = habilitado, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = MaterialTheme.shapes.extraSmall) {
                Text(opciones.firstOrNull { it.first == valor }?.second ?: "Seleccionar ${label.lowercase()}", Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, null)
            }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { opcion -> DropdownMenuItem(text = { Text(opcion.second) }, onClick = { expandido = false; seleccionar(opcion.first) }) }
            }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun Campo(label: String, valor: String, error: String?, habilitado: Boolean, ayuda: String, editar: (String) -> Unit) {
    OutlinedTextField(value = valor, onValueChange = editar, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
        enabled = habilitado, singleLine = true, isError = error != null, supportingText = { Text(error ?: ayuda) })
}
