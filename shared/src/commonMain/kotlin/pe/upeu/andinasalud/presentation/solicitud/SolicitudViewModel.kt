package pe.upeu.andinasalud.presentation.solicitud

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.*
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.components.CargaViewModel

data class FormularioUiState(
    val solicitud: SolicitudCita = SolicitudCita(),
    val errores: Map<CampoSolicitud, String> = emptyMap(),
    val mensaje: String = "",
    val enviando: Boolean = false,
    val citaCreadaId: String? = null,
)

class SolicitudViewModel(obtener: ObtenerCatalogoUseCase, private val solicitar: SolicitarCitaUseCase) :
    CargaViewModel<Catalogo>({ obtener() }, { it.especialidades.isEmpty() || it.sedes.isEmpty() || it.medicos.isEmpty() }) {
    private val _formulario = MutableStateFlow(FormularioUiState())
    val formulario = _formulario.asStateFlow()

    fun editar(campo: CampoSolicitud, valor: String) {
        if (_formulario.value.enviando) return
        _formulario.update {
            val solicitud = when (campo) {
                CampoSolicitud.ESPECIALIDAD -> it.solicitud.copy(especialidadId = valor)
                CampoSolicitud.SEDE -> it.solicitud.copy(sedeId = valor)
                CampoSolicitud.FECHA -> it.solicitud.copy(fecha = valor)
                CampoSolicitud.HORA -> it.solicitud.copy(hora = valor)
                CampoSolicitud.MOTIVO -> it.solicitud.copy(motivo = valor)
            }
            it.copy(solicitud = solicitud, errores = it.errores - campo, mensaje = "")
        }
    }

    fun enviar() {
        if (_formulario.value.enviando || _formulario.value.citaCreadaId != null) return
        _formulario.update { it.copy(enviando = true, errores = emptyMap(), mensaje = "") }
        viewModelScope.launch {
            try {
                when (val resultado = solicitar(_formulario.value.solicitud)) {
                    is ResultadoOperacion.Exito -> _formulario.update { it.copy(enviando = false, citaCreadaId = resultado.cita.id) }
                    is ResultadoOperacion.Invalido -> _formulario.update { it.copy(enviando = false, errores = resultado.campos, mensaje = resultado.mensaje) }
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                _formulario.update { it.copy(enviando = false, mensaje = "No pudimos guardar la cita. Inténtalo de nuevo.") }
            }
        }
    }
}
