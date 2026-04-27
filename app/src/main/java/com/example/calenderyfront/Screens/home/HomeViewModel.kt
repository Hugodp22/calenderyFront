package com.example.calenderyfront.Screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.calenderyfront.common.Result

class HomeViewModel(
    private val repository: PublicacionRepository
) : ViewModel() {

    /**
     * State principal de la pantalla Home
     */
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    /**
     * Inicialización del ViewModel
     * Carga automática de publicaciones
     */
    init {
        loadPosts()
    }

    /**
     * Llamada a backend para obtener publicaciones
     * Ejecutado en hilo IO para evitar bloquear UI
     */
    fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO) {

            /**
             * Activamos estado de carga
             */
            _uiState.update {
                it.copy(isLoading = true, error = null) }

            /**
             * Llamada al repository
             */
            when (val result = repository.getPosts()) {

                /**
                 * Caso éxito
                 */
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            posts = result.data
                        )
                    }
                }

                /**
                 * Caso error
                 */
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message
                        )
                    }
                }
            }
        }
    }
}