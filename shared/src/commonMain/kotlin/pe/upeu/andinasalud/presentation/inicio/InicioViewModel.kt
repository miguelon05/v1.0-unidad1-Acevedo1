package pe.upeu.andinasalud.presentation.inicio

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.usecase.InicioDatos
import pe.upeu.andinasalud.domain.usecase.ObtenerInicioUseCase
import pe.upeu.andinasalud.presentation.components.CargaViewModel

class InicioViewModel(obtener: ObtenerInicioUseCase) : CargaViewModel<InicioDatos>({ obtener() }) {
    init { viewModelScope.launch { obtener.revision.drop(1).collect { recargar() } } }
}
