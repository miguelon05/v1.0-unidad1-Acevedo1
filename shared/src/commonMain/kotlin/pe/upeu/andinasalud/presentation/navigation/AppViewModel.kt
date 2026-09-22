package pe.upeu.andinasalud.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.ReglasCita

data class AppNavState(
    val programadas: Int = 0,
    val limiteAlcanzado: Boolean = false
)

class AppViewModel(private val obtener: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(AppNavState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            obtener.revision.collect { recargar() }
        }
        recargar()
    }

    private fun recargar() {
        viewModelScope.launch {
            val citas = obtener()
            val programadas = citas.count { it.estado is EstadoCita.Programada }
            _uiState.update {
                it.copy(
                    programadas = programadas,
                    limiteAlcanzado = programadas >= ReglasCita.MAX_PROGRAMADAS
                )
            }
        }
    }
}
