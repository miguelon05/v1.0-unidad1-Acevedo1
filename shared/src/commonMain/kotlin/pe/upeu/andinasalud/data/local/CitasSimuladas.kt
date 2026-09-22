package pe.upeu.andinasalud.data.local

import kotlinx.datetime.*
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.Reloj

object CitasSimuladas {
    val paciente = Paciente("P-0417", "Miguel acevedo ", "70622248", "acevedo.doza@correo.pe", "987 654 321")
    val sedes = listOf("Ñaña", "Chosica", "Chaclacayo", "Santa Anita").mapIndexed { i, nombre -> Sede("S${i + 1}", nombre) }
    val especialidades = listOf("Medicina General", "Odontología", "Pediatría", "Nutrición", "Psicología")
        .mapIndexed { i, nombre -> Especialidad("E${i + 1}", nombre) }
    private val nombres = listOf(
        "Dr. Iván Rojas", "Dra. Elena Salas", "Dra. Rosa Flores", "Dr. José Medina",
        "Dra. Carla Núñez", "Dr. Diego Torres", "Lic. Ana Bermúdez", "Lic. Pablo Soto",
        "Ps. Luis Tapia", "Ps. María Pérez",
    )
    val medicos = nombres.mapIndexed { i, nombre ->
        Medico("M${i + 1}", nombre, especialidades[i / 2], if (i % 2 == 0) sedes.take(2) else sedes.drop(2))
    }
    val catalogo = Catalogo(sedes, especialidades, medicos)

    fun crearCitas(reloj: Reloj): List<Cita> {
        val hoy = reloj.ahora().toLocalDateTime(reloj.zona).date
        fun cita(id: Int, medico: Medico, dias: Int, hora: Int, estado: EstadoCita, modalidad: Modalidad) = Cita(
            id.toString(), paciente.id, medico, medico.sedes.first(),
            LocalDateTime(hoy.plus(dias, DateTimeUnit.DAY), LocalTime(hora, 0)),
            "Consulta de control y seguimiento", "Llega 15 minutos antes y trae tu documento de identidad.", estado,
            modalidad
        )
        return listOf(
            cita(1, medicos[0], 2, 9, EstadoCita.Programada(true), Modalidad.PRESENCIAL),
            cita(2, medicos[2], 4, 16, EstadoCita.Programada(false), Modalidad.TELECONSULTA),
            cita(3, medicos[7], 7, 11, EstadoCita.Programada(true), Modalidad.PRESENCIAL),
            cita(6, medicos[0], -8, 10, EstadoCita.Cancelada("Viaje del paciente", true), Modalidad.TELECONSULTA),
        )
    }
}
