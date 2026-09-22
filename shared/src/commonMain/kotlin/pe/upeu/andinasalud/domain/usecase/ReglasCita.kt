package pe.upeu.andinasalud.domain.usecase

import kotlinx.datetime.*
import pe.upeu.andinasalud.domain.model.*
import kotlin.time.Duration.Companion.hours

class ReglasCita(private val reloj: Reloj) {
    companion object { const val MAX_PROGRAMADAS = 3 }

    fun validarSolicitud(solicitud: SolicitudCita, pacienteId: String, citas: List<Cita>, catalogo: Catalogo): ResultadoOperacion.Invalido? {
        val errores = mutableMapOf<CampoSolicitud, String>()
        if (catalogo.especialidades.none { it.id == solicitud.especialidadId }) {
            errores[CampoSolicitud.ESPECIALIDAD] = "Selecciona una especialidad."
        }
        if (catalogo.sedes.none { it.id == solicitud.sedeId }) {
            errores[CampoSolicitud.SEDE] = "Selecciona una sede."
        } else if (solicitud.especialidadId.isNotBlank() && catalogo.medicos.none {
                it.especialidad.id == solicitud.especialidadId && it.sedes.any { sede -> sede.id == solicitud.sedeId }
            }) {
            errores[CampoSolicitud.SEDE] = "No hay médico de esa especialidad en esta sede."
        }
        val fecha = runCatching { LocalDate.parse(solicitud.fecha) }.getOrNull()
        val hora = runCatching { LocalTime.parse(solicitud.hora) }.getOrNull()
        if (fecha == null) errores[CampoSolicitud.FECHA] = "Ingresa una fecha válida: AAAA-MM-DD."
        if (hora == null || !Regex("\\d{2}:\\d{2}").matches(solicitud.hora)) {
            errores[CampoSolicitud.HORA] = "Ingresa una hora válida: HH:MM."
        }
        // RN-04: los espacios al inicio y al final no cuentan como motivo.
        if (solicitud.motivo.trim().length !in 10..200) {
            errores[CampoSolicitud.MOTIVO] = "El motivo debe tener entre 10 y 200 caracteres."
        }
        val programadas = citas.filter { it.pacienteId == pacienteId && it.estado is EstadoCita.Programada }
        if (fecha != null && hora != null) {
            val momento = LocalDateTime(fecha, hora)
            // RN-01: comparar instantes, no cadenas de texto.
            if (momento.toInstant(reloj.zona) < reloj.ahora()) {
                errores[CampoSolicitud.FECHA] = "La cita no puede estar en el pasado."
                errores[CampoSolicitud.HORA] = "Selecciona una fecha y hora futuras."
            }
            // RN-05: la unicidad se aplica solo a las citas Programadas del paciente.
            if (programadas.any { it.fechaHora == momento }) {
                errores[CampoSolicitud.HORA] = "Ya tienes una cita programada en ese día y hora."
            }
        }
        // RN-02: la fuente de verdad del límite está en dominio.
        val limite = programadas.size >= MAX_PROGRAMADAS
        return if (errores.isNotEmpty() || limite) ResultadoOperacion.Invalido(
            errores,
            if (limite) "Ya tienes tres citas programadas. Cancela una elegible antes de solicitar otra."
            else "Revisa los campos indicados.",
        ) else null
    }

    fun impedimentoCancelacion(cita: Cita): String? = when {
        // RN-03: exactamente 24 horas tampoco está permitido.
        cita.estado !is EstadoCita.Programada -> "Solo puedes cancelar citas programadas."
        cita.fechaHora.toInstant(reloj.zona) - reloj.ahora() <= 24.hours ->
            "La cancelación requiere más de 24 horas de anticipación."
        else -> null
    }
}
