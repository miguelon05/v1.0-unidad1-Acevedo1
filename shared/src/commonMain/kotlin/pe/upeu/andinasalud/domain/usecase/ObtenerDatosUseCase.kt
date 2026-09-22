package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerPacienteUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke() = repository.obtenerPaciente()
}

class ObtenerCatalogoUseCase(private val repository: CitaRepository) {
    suspend operator fun invoke() = repository.obtenerCatalogo()
}

data class InicioDatos(val paciente: Paciente, val proxima: Cita?)

class ObtenerInicioUseCase(private val repository: CitaRepository, private val reloj: Reloj) {
    val revision get() = repository.revision
    suspend operator fun invoke(): InicioDatos? {
        val paciente = repository.obtenerPaciente() ?: return null
        val proxima = repository.obtenerCitas().filter {
            it.pacienteId == paciente.id && it.estado is EstadoCita.Programada && it.fechaHora.toInstant(reloj.zona) >= reloj.ahora()
        }.minByOrNull { it.fechaHora }
        return InicioDatos(paciente, proxima)
    }
}

data class DetalleDatos(val cita: Cita, val impedimentoCancelacion: String?)

class ObtenerDetalleUseCase(private val repository: CitaRepository, private val reglas: ReglasCita) {
    suspend operator fun invoke(id: String): DetalleDatos? {
        val paciente = repository.obtenerPaciente() ?: return null
        return repository.obtenerCitas().firstOrNull { it.id == id && it.pacienteId == paciente.id }
            ?.let { DetalleDatos(it, reglas.impedimentoCancelacion(it)) }
    }
}
