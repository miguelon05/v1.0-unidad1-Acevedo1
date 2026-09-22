package pe.upeu.andinasalud.presentation.detalle

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.ResultadoOperacion
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.components.CargaViewModel

data class CancelacionUiState(val procesando: Boolean = false, val mensaje: String = "")

class DetalleCitaViewModel(
    private val id: String,
    obtener: ObtenerDetalleUseCase,
    private val cancelarCita: CancelarCitaUseCase,
) : CargaViewModel<DetalleDatos>({ obtener(id) }) {
    private val _cancelacion = MutableStateFlow(CancelacionUiState())
    val cancelacion = _cancelacion.asStateFlow()

    fun cancelar() {
        if (_cancelacion.value.procesando) return
        _cancelacion.value = CancelacionUiState(procesando = true)
        viewModelScope.launch {
            try {
                when (val resultado = cancelarCita(id)) {
                    is ResultadoOperacion.Exito -> {
                        _cancelacion.value = CancelacionUiState(mensaje = "Tu cita ha sido cancelada.")
                        recargar()
                    }
                    is ResultadoOperacion.Invalido -> _cancelacion.value = CancelacionUiState(mensaje = resultado.mensaje)
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                _cancelacion.value = CancelacionUiState(mensaje = "No pudimos cancelar la cita. Vuelve a intentarlo.")
            }
        }
    }
}
