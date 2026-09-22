package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDateTime

data class  Cita(
    val id: String,
    val pacienteId: String,
    val medico: Medico,
    val sede: Sede,
    val fechaHora: LocalDateTime,
    val motivo: String,
    val indicaciones: String,
    val estado: EstadoCita,
    val modalidad: Modalidad = Modalidad.PRESENCIAL
)
