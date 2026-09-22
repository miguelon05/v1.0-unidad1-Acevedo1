package pe.upeu.andinasalud.data.local

/** Permite verificar RF-08 de forma reproducible sin servicios ni fallos aleatorios. */
class ControlSimulacion {
    var vacio: Boolean = false
        private set
    private var fallarProximaLectura = false

    fun configurar(escenario: String) {
        vacio = escenario == "vacio"
        fallarProximaLectura = escenario == "error"
    }

    fun comprobarLectura() {
        if (fallarProximaLectura) {
            fallarProximaLectura = false
            error("Fallo de lectura simulado para RF-08")
        }
    }
}
