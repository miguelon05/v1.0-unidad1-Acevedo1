@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package pe.upeu.andinasalud.presentation

import androidx.lifecycle.ViewModelStore
import kotlin.test.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.RelojFijo
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.components.*
import pe.upeu.andinasalud.presentation.citas.*
import pe.upeu.andinasalud.presentation.detalle.*
import pe.upeu.andinasalud.presentation.inicio.*
import pe.upeu.andinasalud.presentation.perfil.*
import pe.upeu.andinasalud.presentation.solicitud.*

private class RepositorioControlado : CitaRepository {
    val base = CitaRepositoryFake(RelojFijo())
    var falla = false
    var vacio = false
    override val revision = MutableStateFlow(0L)
    private fun comprobar() { if (falla) error("Error simulado") }
    override suspend fun obtenerPaciente(): Paciente? { comprobar(); return if (vacio) null else base.obtenerPaciente() }
    override suspend fun obtenerCatalogo(): Catalogo { comprobar(); return if (vacio) Catalogo(emptyList(), emptyList(), emptyList()) else base.obtenerCatalogo() }
    override suspend fun obtenerCitas(): List<Cita> { comprobar(); return if (vacio) emptyList() else base.obtenerCitas() }
    override suspend fun guardar(cita: Cita) { comprobar(); base.guardar(cita); revision.value++ }
}

class ViewModelsTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    @BeforeTest fun preparar() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun limpiar() { store.clear(); Dispatchers.resetMain() }
    private fun <T : androidx.lifecycle.ViewModel> conservar(vm: T): T { store.put(vm.toString(), vm); return vm }

    @Test fun cargaDura800MilisegundosSinBloquear() = runTest(dispatcher) {
        val vm = conservar(CargaViewModel({ "dato" }))
        assertIs<UiState.Cargando>(vm.uiState.value)
        advanceTimeBy(799); runCurrent()
        assertIs<UiState.Cargando>(vm.uiState.value)
        advanceTimeBy(1); runCurrent()
        assertEquals("dato", assertIs<UiState.Contenido<String>>(vm.uiState.value).datos)
    }
    @Test fun todasLasPantallasDeDatosMuestranErrorYPermitenReintentar() = runTest(dispatcher) {
        val repo = RepositorioControlado().apply { falla = true }
        val reloj = RelojFijo()
        val reglas = ReglasCita(reloj)
        val coordinador = CoordinadorCitas()
        val inicio = conservar(InicioViewModel(ObtenerInicioUseCase(repo, reloj)))
        val perfil = conservar(PerfilViewModel(ObtenerPacienteUseCase(repo)))
        val detalle = conservar(DetalleCitaViewModel("1", ObtenerDetalleUseCase(repo, reglas), CancelarCitaUseCase(repo, reglas, coordinador)))
        val solicitud = conservar(SolicitudViewModel(ObtenerCatalogoUseCase(repo), SolicitarCitaUseCase(repo, reglas, coordinador)))
        val citas = conservar(CitasViewModel(ObtenerCitasUseCase(repo)))
        advanceUntilIdle()
        listOf(inicio.uiState.value, perfil.uiState.value, detalle.uiState.value, solicitud.uiState.value, citas.uiState.value.resultado)
            .forEach { assertIs<UiState.Error>(it) }
        repo.falla = false
        inicio.recargar(); perfil.recargar(); detalle.recargar(); solicitud.recargar(); citas.recargar()
        advanceUntilIdle()
        listOf(inicio.uiState.value, perfil.uiState.value, detalle.uiState.value, solicitud.uiState.value, citas.uiState.value.resultado)
            .forEach { assertIs<UiState.Contenido<*>>(it) }
    }
    @Test fun todasLasPantallasDeDatosTienenEstadoVacio() = runTest(dispatcher) {
        val repo = RepositorioControlado().apply { vacio = true }
        val reglas = ReglasCita(RelojFijo())
        val coordinador = CoordinadorCitas()
        val inicio = conservar(InicioViewModel(ObtenerInicioUseCase(repo, RelojFijo())))
        val perfil = conservar(PerfilViewModel(ObtenerPacienteUseCase(repo)))
        val detalle = conservar(DetalleCitaViewModel("1", ObtenerDetalleUseCase(repo, reglas), CancelarCitaUseCase(repo, reglas, coordinador)))
        val solicitud = conservar(SolicitudViewModel(ObtenerCatalogoUseCase(repo), SolicitarCitaUseCase(repo, reglas, coordinador)))
        val citas = conservar(CitasViewModel(ObtenerCitasUseCase(repo)))
        advanceUntilIdle()
        listOf(inicio.uiState.value, perfil.uiState.value, detalle.uiState.value, solicitud.uiState.value, citas.uiState.value.resultado)
            .forEach { assertIs<UiState.Vacio>(it) }
    }
    @Test fun cancelarActualizaInicioYListaSinRecrearViewModels() = runTest(dispatcher) {
        val repo = RepositorioControlado()
        val reglas = ReglasCita(RelojFijo())
        val inicio = conservar(InicioViewModel(ObtenerInicioUseCase(repo, RelojFijo())))
        val citas = conservar(CitasViewModel(ObtenerCitasUseCase(repo)))
        advanceUntilIdle()
        assertEquals("1", assertIs<UiState.Contenido<InicioDatos>>(inicio.uiState.value).datos.proxima?.id)
        CancelarCitaUseCase(repo, reglas, CoordinadorCitas())("1")
        advanceUntilIdle()
        assertEquals("2", assertIs<UiState.Contenido<InicioDatos>>(inicio.uiState.value).datos.proxima?.id)
        citas.filtrar(FiltroEstado.PROGRAMADA)
        assertEquals(2, assertIs<UiState.Contenido<List<Cita>>>(citas.uiState.value.resultado).datos.size)
    }
    @Test fun filtrosSeCombinanYConservanEstadoVacio() = runTest(dispatcher) {
        val vm = conservar(CitasViewModel(ObtenerCitasUseCase(RepositorioControlado())))
        advanceUntilIdle()
        vm.buscar("IVAN"); vm.filtrar(FiltroEstado.PROGRAMADA)
        assertEquals(1, assertIs<UiState.Contenido<List<Cita>>>(vm.uiState.value.resultado).datos.size)
        vm.filtrar(FiltroEstado.ATENDIDA)
        assertIs<UiState.Vacio>(vm.uiState.value.resultado)
        vm.buscar("")
        assertEquals(2, assertIs<UiState.Contenido<List<Cita>>>(vm.uiState.value.resultado).datos.size)
    }
    @Test fun formularioPropagaErroresPorCampo() = runTest(dispatcher) {
        val repo = RepositorioControlado()
        val vm = conservar(SolicitudViewModel(ObtenerCatalogoUseCase(repo), SolicitarCitaUseCase(repo, ReglasCita(RelojFijo()), CoordinadorCitas())))
        advanceUntilIdle(); vm.enviar(); advanceUntilIdle()
        assertEquals(5, vm.formulario.value.errores.size)
        assertFalse(vm.formulario.value.enviando)
        vm.editar(CampoSolicitud.MOTIVO, "Control de salud")
        assertFalse(CampoSolicitud.MOTIVO in vm.formulario.value.errores)
    }
    @Test fun destruirViewModelCancelaCargaPendiente() = runTest(dispatcher) {
        var leido = false
        conservar(CargaViewModel({ leido = true; "dato" }))
        runCurrent(); store.clear(); advanceUntilIdle()
        assertFalse(leido)
    }
}
