package pe.upeu.andinasalud.domain.model

data class Catalogo(val sedes: List<Sede>, val especialidades: List<Especialidad>, val medicos: List<Medico>)

data class SolicitudCita(
    val especialidadId: String = "",
    val sedeId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
)

enum class CampoSolicitud { ESPECIALIDAD, SEDE, FECHA, HORA, MOTIVO }

sealed interface ResultadoOperacion {
    data class Exito(val cita: Cita) : ResultadoOperacion
    data class Invalido(
        val campos: Map<CampoSolicitud, String> = emptyMap(),
        val mensaje: String = "Revisa los campos indicados.",
    ) : ResultadoOperacion
}
