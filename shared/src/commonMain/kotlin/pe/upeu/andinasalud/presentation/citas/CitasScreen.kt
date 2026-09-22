package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.usecase.FiltroEstado
import pe.upeu.andinasalud.presentation.components.*

@Composable
fun CitasScreen(estado: CitasUiState, buscar: (String) -> Unit, filtrar: (FiltroEstado) -> Unit, reintentar: () -> Unit, detalle: (String) -> Unit, solicitar: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = estado.busqueda, onValueChange = buscar, modifier = Modifier.fillMaxWidth(),
                label = { Text("Especialidad o médico") }, leadingIcon = { Icon(Icons.Outlined.Search, null) }, singleLine = true)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FiltroEstado.entries.forEach { filtro ->
                    val label = when (filtro) {
                        FiltroEstado.TODAS -> "Todas"
                        FiltroEstado.PROGRAMADA -> "Programada"
                        FiltroEstado.ATENDIDA -> "Atendida"
                        FiltroEstado.CANCELADA -> "Cancelada"
                    }
                    FilterChip(selected = estado.filtro == filtro, onClick = { filtrar(filtro) }, label = { Text(label) })
                }
            }
            FilledTonalButton(onClick = solicitar, modifier = Modifier.fillMaxWidth()) { Text("Solicitar cita") }
        }
        Box(Modifier.weight(1f)) {
            EstadoContenido(estado.resultado, reintentar, "No encontramos citas", "Prueba otra búsqueda o cambia el filtro de estado.") { citas ->
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(citas, key = { it.id }) { cita -> CitaCard(cita, { detalle(cita.id) }) }
                }
            }
        }
    }
}
