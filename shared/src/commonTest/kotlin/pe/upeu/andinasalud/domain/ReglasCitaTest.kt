package pe.upeu.andinasalud.domain

import kotlin.test.*
import kotlin.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.*

class RelojFijo : Reloj {
    override fun ahora() = Instant.parse("2026-09-22T15:00:00Z")
    override val zona = TimeZone.of("America/Lima")
}

class ReglasCitaTest {
    private val reloj = RelojFijo()
    private val reglas = ReglasCita(reloj)
    private val catalogo = CitasSimuladas.catalogo
    private val citas = CitasSimuladas.crearCitas(reloj)
    private val paciente = CitasSimuladas.paciente.id
    private val solicitud = SolicitudCita("E1", "S1", "2026-10-01", "09:00", "Control general de salud")

    @Test fun rn01RechazaFechaPasada() {
        val error = reglas.validarSolicitud(solicitud.copy(fecha = "2026-09-21"), paciente, emptyList(), catalogo)
        assertTrue(CampoSolicitud.FECHA in assertNotNull(error).campos)
    }
    @Test fun rn01RechazaHoraPasadaDelMismoDia() {
        val error = reglas.validarSolicitud(solicitud.copy(fecha = "2026-09-22", hora = "09:59"), paciente, emptyList(), catalogo)
        assertTrue(CampoSolicitud.HORA in assertNotNull(error).campos)
    }
    @Test fun rn01AceptaMomentoActual() {
        assertNull(reglas.validarSolicitud(solicitud.copy(fecha = "2026-09-22", hora = "10:00"), paciente, emptyList(), catalogo))
    }
    @Test fun rn02RechazaCuartaProgramada() {
        assertTrue(assertNotNull(reglas.validarSolicitud(solicitud, paciente, citas, catalogo)).mensaje.contains("tres"))
    }
    @Test fun rn02NoCuentaAtendidasCanceladasNiOtroPaciente() {
        val historial = citas.filterNot { it.estado is EstadoCita.Programada } + citas.first().copy(pacienteId = "OTRO")
        assertNull(reglas.validarSolicitud(solicitud, paciente, historial, catalogo))
    }
    @Test fun rn03RechazaExactamente24Horas() {
        assertNotNull(reglas.impedimentoCancelacion(citas.first().copy(fechaHora = LocalDateTime.parse("2026-09-23T10:00"))))
    }
    @Test fun rn03AceptaMasDe24Horas() {
        assertNull(reglas.impedimentoCancelacion(citas.first().copy(fechaHora = LocalDateTime.parse("2026-09-23T10:01"))))
    }
    @Test fun rn03RechazaMenosDe24HorasYEstadosNoProgramados() {
        assertNotNull(reglas.impedimentoCancelacion(citas.first().copy(fechaHora = LocalDateTime.parse("2026-09-23T09:59"))))
        citas.filterNot { it.estado is EstadoCita.Programada }.forEach { assertNotNull(reglas.impedimentoCancelacion(it)) }
    }
    @Test fun rn04AceptaLimites10Y200() {
        listOf(10, 200).forEach { assertNull(reglas.validarSolicitud(solicitud.copy(motivo = "a".repeat(it)), paciente, emptyList(), catalogo)) }
    }
    @Test fun rn04Rechaza9Y201YEspacios() {
        listOf("a".repeat(9), "a".repeat(201), "          ").forEach {
            assertTrue(CampoSolicitud.MOTIVO in assertNotNull(reglas.validarSolicitud(solicitud.copy(motivo = it), paciente, emptyList(), catalogo)).campos)
        }
    }
    @Test fun rn05RechazaMismoPacienteDiaHora() {
        val duplicada = solicitud.copy(fecha = citas.first().fechaHora.date.toString(), hora = "09:00")
        assertTrue(CampoSolicitud.HORA in assertNotNull(reglas.validarSolicitud(duplicada, paciente, citas.take(1), catalogo)).campos)
    }
    @Test fun rn05AceptaHorarioDeCitaCancelada() {
        val cancelada = citas.first().copy(estado = EstadoCita.Cancelada("Viaje", true))
        assertNull(reglas.validarSolicitud(solicitud.copy(fecha = cancelada.fechaHora.date.toString(), hora = "09:00"), paciente, listOf(cancelada), catalogo))
    }
    @Test fun validaTodosLosCamposVacios() {
        assertEquals(CampoSolicitud.entries.toSet(), assertNotNull(reglas.validarSolicitud(SolicitudCita(), paciente, emptyList(), catalogo)).campos.keys)
    }
    @Test fun rechazaFechaInexistenteYHoraInvalida() {
        val error = assertNotNull(reglas.validarSolicitud(solicitud.copy(fecha = "2026-02-30", hora = "25:00"), paciente, emptyList(), catalogo))
        assertTrue(CampoSolicitud.FECHA in error.campos)
        assertTrue(CampoSolicitud.HORA in error.campos)
    }
    @Test fun rechazaSedeSinMedicoDisponible() {
        val reducido = catalogo.copy(medicos = catalogo.medicos.take(1))
        assertTrue(CampoSolicitud.SEDE in assertNotNull(reglas.validarSolicitud(solicitud.copy(sedeId = "S4"), paciente, emptyList(), reducido)).campos)
    }
    @Test fun busquedaIgnoraMayusculasTildesYUnicodeDescompuesto() = runTest {
        val obtener = ObtenerCitasUseCase(CitaRepositoryFake(reloj))
        assertEquals(2, obtener(busqueda = "IVAN").size)
        assertEquals(1, obtener(busqueda = "ODONTOLOGI\u0301A").size)
        assertEquals(1, obtener(FiltroEstado.ATENDIDA, "psicologia").size)
        assertTrue(obtener(FiltroEstado.CANCELADA, "psicologia").isEmpty())
    }
    @Test fun listaOrdenadaCronologicamente() = runTest {
        val resultado = ObtenerCitasUseCase(CitaRepositoryFake(reloj))()
        assertEquals(resultado.sortedBy { it.fechaHora }, resultado)
    }
    @Test fun semillaCumpleMinimosYFechasFuturas() {
        assertEquals(6, citas.size)
        assertEquals(4, catalogo.sedes.size)
        assertEquals(5, catalogo.especialidades.size)
        catalogo.especialidades.forEach { e -> assertTrue(catalogo.medicos.count { it.especialidad == e } >= 2) }
        assertEquals(3, citas.count { it.estado is EstadoCita.Programada })
        assertEquals(2, citas.count { it.estado is EstadoCita.Atendida })
        assertEquals(1, citas.count { it.estado is EstadoCita.Cancelada })
        citas.filter { it.estado is EstadoCita.Programada }.forEach { assertTrue(it.fechaHora.toInstant(reloj.zona) > reloj.ahora()) }
    }
    @Test fun cancelarLiberaCupoYSolicitarAsignaMedicoDeSede() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val coordinador = CoordinadorCitas()
        assertIs<ResultadoOperacion.Exito>(CancelarCitaUseCase(repo, reglas, coordinador)("1"))
        val nueva = assertIs<ResultadoOperacion.Exito>(SolicitarCitaUseCase(repo, reglas, coordinador)(solicitud.copy(sedeId = "S4"))).cita
        assertTrue(nueva.sede in nueva.medico.sedes)
        assertEquals(3, repo.obtenerCitas().count { it.estado is EstadoCita.Programada })
        assertEquals(2L, repo.revision.value)
    }
    @Test fun solicitudesConcurrentesNoSuperanLimite() = runTest {
        val repo = CitaRepositoryFake(reloj)
        val coordinador = CoordinadorCitas()
        CancelarCitaUseCase(repo, reglas, coordinador)("1")
        val solicitar = SolicitarCitaUseCase(repo, reglas, coordinador)
        val resultados = listOf(async { solicitar(solicitud) }, async { solicitar(solicitud.copy(hora = "11:00")) }).awaitAll()
        assertEquals(1, resultados.count { it is ResultadoOperacion.Exito })
        assertEquals(3, repo.obtenerCitas().count { it.estado is EstadoCita.Programada })
    }
}
