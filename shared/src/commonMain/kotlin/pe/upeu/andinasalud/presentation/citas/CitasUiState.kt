package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.FiltroEstado
import pe.upeu.andinasalud.presentation.components.UiState

data class CitasUiState(
    val resultado: UiState<List<Cita>> = UiState.Cargando,
    val busqueda: String = "",
    val filtro: FiltroEstado = FiltroEstado.TODAS,
)
