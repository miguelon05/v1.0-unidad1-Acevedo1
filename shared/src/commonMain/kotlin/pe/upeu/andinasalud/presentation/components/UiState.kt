package pe.upeu.andinasalud.presentation.components

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data object Vacio : UiState<Nothing>
    data class Contenido<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}
