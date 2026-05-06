package com.example.calenderyfront.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Selection
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.pageSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class SelectionViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val chatOption = path.toRoute<Selection>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).chatOption

    private val _uiState = MutableStateFlow(SelectionUiState(userInfo, chatOption))
    val uiState: StateFlow<SelectionUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<SelectionState>(SelectionState.Iniciado)
    val state: StateFlow<SelectionState> = _state.asStateFlow()

    private var currentPageSelectionUsers = 0
    private val currentPageSize = pageSize

    init {

    }

    fun onSearchChange(searchName: String) {
        _uiState.update { it.copy(
            searchName = searchName,
            selectionUsersList = emptyList()
        )}
    }

    fun searchUserByName() {
        val currentState = _uiState.value

        if (_state.value is SelectionState.Cargando || currentState.lastPage) {
            return
        }

        _state.value = SelectionState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.obtenerUsuariosBuscados(
                    idUsuario = userInfo.idUsuario,
                    searchName = currentState.searchName,
                    page = currentPageSelectionUsers
                )

                if (respuesta.isSuccessful) {
                    val usuariosCargados = respuesta.body()

                    if (usuariosCargados != null) {
                        _uiState.update { it.copy(
                            selectionUsersList = it.selectionUsersList + usuariosCargados.content,
                            lastPage = usuariosCargados.content.size < currentPageSize
                        )}

                        _state.value = SelectionState.PaginaCargada
                        currentPageSelectionUsers++
                    }
                }

                else {
                    _state.value = SelectionState.Error(errorMessages(respuesta.code()))
                }

            }
            catch (e: Exception) {
                _state.value = SelectionState.Error(R.string.Error_Network)
            }
        }

    }
}