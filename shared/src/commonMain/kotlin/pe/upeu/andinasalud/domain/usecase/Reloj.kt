package pe.upeu.andinasalud.domain.usecase

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

interface Reloj {
    fun ahora(): Instant
    val zona: TimeZone
}

class RelojSistema : Reloj {
    override fun ahora() = Clock.System.now()
    // Todas las sedes están en Lima. La validación no depende de la zona del dispositivo.
    override val zona = TimeZone.of("America/Lima")
}
