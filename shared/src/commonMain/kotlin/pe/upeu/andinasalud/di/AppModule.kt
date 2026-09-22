package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.*

val appModule = module {
    single<Reloj> { RelojSistema() }
    single<CitaRepository> { CitaRepositoryFake(get()) }
    single { ReglasCita(get()) }
    single { CoordinadorCitas() }
    factory { ObtenerCitasUseCase(get()) }
    factory { ObtenerPacienteUseCase(get()) }
    factory { ObtenerCatalogoUseCase(get()) }
    factory { ObtenerInicioUseCase(get(), get()) }
    factory { ObtenerDetalleUseCase(get(), get()) }
    factory { SolicitarCitaUseCase(get(), get(), get()) }
    factory { CancelarCitaUseCase(get(), get(), get()) }
}

fun initKoin(config: KoinAppDeclaration = {}) = startKoin {
    config()
    modules(appModule)
}
