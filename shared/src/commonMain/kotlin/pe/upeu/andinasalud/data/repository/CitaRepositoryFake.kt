package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.data.local.ControlSimulacion
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.Reloj

class CitaRepositoryFake(reloj: Reloj, private val simulacion: ControlSimulacion = ControlSimulacion()) : CitaRepository {
    private val citas = CitasSimuladas.crearCitas(reloj).toMutableList()
    private val _revision = MutableStateFlow(0L)
    override val revision = _revision.asStateFlow()

    override suspend fun obtenerPaciente(): Paciente? {
        simulacion.comprobarLectura()
        return if (simulacion.vacio) null else CitasSimuladas.paciente
    }
    override suspend fun obtenerCatalogo(): Catalogo {
        simulacion.comprobarLectura()
        return if (simulacion.vacio) Catalogo(emptyList(), emptyList(), emptyList()) else CitasSimuladas.catalogo
    }
    override suspend fun obtenerCitas(): List<Cita> {
        simulacion.comprobarLectura()
        return if (simulacion.vacio) emptyList() else citas.toList()
    }

    override suspend fun guardar(cita: Cita) {
        val indice = citas.indexOfFirst { it.id == cita.id }
        if (indice >= 0) citas[indice] = cita else citas.add(cita)
        _revision.value += 1
    }
}
