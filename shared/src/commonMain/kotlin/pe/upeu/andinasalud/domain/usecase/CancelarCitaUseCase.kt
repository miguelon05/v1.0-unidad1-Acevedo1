package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CancelarCitaUseCase(
    private val repository: CitaRepository,
    private val reglas: ReglasCita,
    private val coordinador: CoordinadorCitas,
) {
    suspend operator fun invoke(id: String): ResultadoOperacion = coordinador.mutex.withLock {
        val paciente = repository.obtenerPaciente()
        val cita = repository.obtenerCitas().firstOrNull { it.id == id && it.pacienteId == paciente?.id }
            ?: return@withLock ResultadoOperacion.Invalido(mensaje = "No se encontró la cita.")
        reglas.impedimentoCancelacion(cita)?.let { return@withLock ResultadoOperacion.Invalido(mensaje = it) }
        val cancelada = cita.copy(estado = EstadoCita.Cancelada("Cancelada a solicitud del paciente", true))
        repository.guardar(cancelada)
        ResultadoOperacion.Exito(cancelada)
    }
}
