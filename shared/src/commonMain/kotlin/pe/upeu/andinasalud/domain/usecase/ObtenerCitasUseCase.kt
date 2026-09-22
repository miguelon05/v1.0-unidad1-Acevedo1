package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import kotlinx.datetime.toLocalDateTime

enum class FiltroEstado { TODAS, PROGRAMADA, ATENDIDA, CANCELADA }

/** Kotlin común: contempla vocales acentuadas y marcas Unicode descompuestas. */
fun String.normalizarBusqueda(): String = lowercase().mapNotNull {
    when (it) {
        in '\u0300'..'\u036f' -> null
        'á', 'à', 'ä', 'â' -> 'a'
        'é', 'è', 'ë', 'ê' -> 'e'
        'í', 'ì', 'ï', 'î' -> 'i'
        'ó', 'ò', 'ö', 'ô' -> 'o'
        'ú', 'ù', 'ü', 'û' -> 'u'
        'ñ' -> 'n'
        else -> it
    }
}.joinToString("").trim()

class ObtenerCitasUseCase(
    private val repository: CitaRepository,
    private val reloj: Reloj
)  {
    val revision get() = repository.revision

    suspend operator fun invoke(filtro: FiltroEstado = FiltroEstado.TODAS, busqueda: String = ""): List<Cita> {
        val paciente = repository.obtenerPaciente() ?: return emptyList()
        return filtrar(repository.obtenerCitas().filter { it.pacienteId == paciente.id }, filtro, busqueda)
    }

    fun filtrar(
        citas: List<Cita>,
        filtro: FiltroEstado,
        busqueda: String,
        soloHoy: Boolean = false
    ): List<Cita> {
        val texto = busqueda.normalizarBusqueda()
        val hoy = reloj.ahora().toLocalDateTime(reloj.zona).date
        return citas.filter { cita ->
            val coincideEstado = when (filtro) {
                FiltroEstado.TODAS -> true
                FiltroEstado.PROGRAMADA -> cita.estado is EstadoCita.Programada
                FiltroEstado.ATENDIDA -> cita.estado is EstadoCita.Atendida
                FiltroEstado.CANCELADA -> cita.estado is EstadoCita.Cancelada
            }

            val coincideHoy = !soloHoy || cita.fechaHora.date == hoy
            coincideEstado &&
                    coincideHoy &&
                    (texto in cita.medico.nombre.normalizarBusqueda() ||
                            texto in cita.medico.especialidad.nombre.normalizarBusqueda())
        }.sortedBy { it.fechaHora }
    }
}
