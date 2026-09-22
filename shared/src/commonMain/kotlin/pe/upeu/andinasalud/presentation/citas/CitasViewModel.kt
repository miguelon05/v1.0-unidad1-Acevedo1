package pe.upeu.andinasalud.presentation.citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.*
import pe.upeu.andinasalud.presentation.components.UiState

class CitasViewModel(private val obtener: ObtenerCitasUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState = _uiState.asStateFlow()
    private var todas = emptyList<Cita>()
    private var carga: Job? = null

    init { viewModelScope.launch { obtener.revision.collect { recargar() } } }

    fun recargar() {
        carga?.cancel()
        _uiState.update { it.copy(resultado = UiState.Cargando) }
        carga = viewModelScope.launch {
            try {
                delay(800)
                todas = obtener()
                aplicarFiltros()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                _uiState.update { it.copy(resultado = UiState.Error("No pudimos cargar tus citas. Inténtalo de nuevo.")) }
            }
        }
    }

    fun buscar(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
        if (_uiState.value.resultado !is UiState.Cargando && _uiState.value.resultado !is UiState.Error) aplicarFiltros()
    }

    fun filtrar(filtro: FiltroEstado) {
        _uiState.update { it.copy(filtro = filtro) }
        if (_uiState.value.resultado !is UiState.Cargando && _uiState.value.resultado !is UiState.Error) aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val estado = _uiState.value
        val visibles = obtener.filtrar(todas, estado.filtro, estado.busqueda)
        _uiState.update { it.copy(resultado = if (visibles.isEmpty()) UiState.Vacio else UiState.Contenido(visibles)) }
    }
}
