package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.data.local.ControlSimulacion
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.inicio.InicioViewModel
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel

val appModule = module {
    single<Reloj> { RelojSistema() }
    single { ControlSimulacion() }
    single<CitaRepository> { CitaRepositoryFake(get(), get()) }
    single { ReglasCita(get()) }
    single { CoordinadorCitas() }
    factory { ObtenerCitasUseCase(get(), get()) }
    factory { ObtenerPacienteUseCase(get()) }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { ObtenerInicioUseCase(get(), get()) }
    factory { ObtenerDetalleUseCase(get(), get()) }
    factory { SolicitarCitaUseCase(get(), get(), get()) }
    factory { CancelarCitaUseCase(get(), get(), get()) }
    viewModel { InicioViewModel(get()) }
    viewModel { CitasViewModel(get()) }
    viewModel { (id: String) -> DetalleCitaViewModel(id, get(), get()) }
    viewModel { SolicitudViewModel(get(), get()) }
    viewModel { PerfilViewModel(get()) }
    viewModel { pe.upeu.andinasalud.presentation.navigation.AppViewModel(get()) }
}

fun initKoin(config: KoinAppDeclaration = {}) = startKoin {
    config()
    modules(appModule)
}
