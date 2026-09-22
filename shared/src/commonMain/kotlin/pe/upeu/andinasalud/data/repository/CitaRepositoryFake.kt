package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.Reloj

class CitaRepositoryFake(reloj: Reloj) : CitaRepository {
    private val citas = CitasSimuladas.crearCitas(reloj).toMutableList()
    private val _revision = MutableStateFlow(0L)
    override val revision = _revision.asStateFlow()

    override suspend fun obtenerPaciente() = CitasSimuladas.paciente
    override suspend fun obtenerCatalogo() = CitasSimuladas.catalogo
    override suspend fun obtenerCitas(): List<Cita> = citas.toList()

    override suspend fun guardar(cita: Cita) {
        val indice = citas.indexOfFirst { it.id == cita.id }
        if (indice >= 0) citas[indice] = cita else citas.add(cita)
        _revision.value += 1
    }
}
