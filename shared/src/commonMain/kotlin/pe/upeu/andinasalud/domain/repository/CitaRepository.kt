package pe.upeu.andinasalud.domain.repository

import kotlinx.coroutines.flow.StateFlow
import pe.upeu.andinasalud.domain.model.*

interface CitaRepository {
    /** Señal de invalidación compartida: una mutación refresca las pantallas activas. */
    val revision: StateFlow<Long>
    suspend fun obtenerPaciente(): Paciente?
    suspend fun obtenerCatalogo(): Catalogo
    suspend fun obtenerCitas(): List<Cita>
    suspend fun guardar(cita: Cita)
}
