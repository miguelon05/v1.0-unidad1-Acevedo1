package pe.upeu.andinasalud.presentation.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** RF-08: una carga suspendida de 800 ms por pantalla, cancelada con el ViewModel. */
open class CargaViewModel<T : Any>(
    private val cargarDatos: suspend () -> T?,
    private val estaVacio: (T) -> Boolean = { false },
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<T>>(UiState.Cargando)
    val uiState = _uiState.asStateFlow()
    private var carga: Job? = null

    init { recargar() }

    fun recargar() {
        carga?.cancel()
        _uiState.value = UiState.Cargando
        carga = viewModelScope.launch {
            try {
                delay(800)
                val datos = cargarDatos()
                _uiState.value = if (datos == null || estaVacio(datos)) UiState.Vacio else UiState.Contenido(datos)
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                _uiState.value = UiState.Error("No pudimos cargar la información. Inténtalo de nuevo.")
            }
        }
    }
}
