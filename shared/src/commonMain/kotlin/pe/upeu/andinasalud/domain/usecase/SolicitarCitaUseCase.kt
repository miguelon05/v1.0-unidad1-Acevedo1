package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDateTime
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository

/** Compartida entre solicitar y cancelar para validar y guardar sin carreras. */
class CoordinadorCitas { val mutex = Mutex() }

class SolicitarCitaUseCase(
    private val repository: CitaRepository,
    private val reglas: ReglasCita,
    private val coordinador: CoordinadorCitas,
) {
    suspend operator fun invoke(solicitud: SolicitudCita): ResultadoOperacion = coordinador.mutex.withLock {
        val paciente = repository.obtenerPaciente()
            ?: return@withLock ResultadoOperacion.Invalido(mensaje = "No se encontró al paciente.")
        val catalogo = repository.obtenerCatalogo()
        val citas = repository.obtenerCitas()
        reglas.validarSolicitud(solicitud, paciente.id, citas, catalogo)?.let { return@withLock it }
        val medico = catalogo.medicos.first {
            it.especialidad.id == solicitud.especialidadId && it.sedes.any { sede -> sede.id == solicitud.sedeId }
        }
        val siguiente = (citas.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0) + 1
        val cita = Cita(
            id = siguiente.toString(), pacienteId = paciente.id, medico = medico,
            sede = catalogo.sedes.first { it.id == solicitud.sedeId },
            fechaHora = LocalDateTime.parse("${solicitud.fecha}T${solicitud.hora}"),
            motivo = solicitud.motivo.trim(), indicaciones = "Llega 15 minutos antes y trae tu documento de identidad.",
            estado = EstadoCita.Programada(recordatorioActivo = true),
        )
        repository.guardar(cita)
        ResultadoOperacion.Exito(cita)
    }
}
