package jafar.cicilan.data.model

sealed class UIState {
    object Loading: UIState()
    object Empty: UIState()
    data class Success<out T>(val data: T): UIState()
    data class Error(val cause: String): UIState()
}
