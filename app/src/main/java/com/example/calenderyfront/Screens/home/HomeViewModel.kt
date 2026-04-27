package com.example.calenderyfront.Screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Settings
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

class HomeViewModel(path: SavedStateHandle): ViewModel(){

    private val userInfo = path.toRoute<Settings>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _uiState = MutableStateFlow(HomeUiState(userInfo,0,emptyList()))
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _state = MutableStateFlow<HomeState>(HomeState.Iniciado)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    var currentPage = 0


    init {
        //loadPosts()
    }

    fun loadPosts() {
        val currentUiState = _uiState.value

        if (_state.value is HomeState.Cargando || currentUiState.ultimaPagina) {
            return
        }
        _state.value = HomeState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val respuesta = RetrofitClient.publicacionApi.obtenerPublicacionesHome(currentPage, pageSize)

                if (respuesta.isSuccessful) {
                    val listaObtenida = respuesta.body()

                    if (listaObtenida != null) {
                        _uiState.update {
                            it.copy(
                                posts = it.posts + listaObtenida,
                                )
                        }
                        currentPage++
                        _state.value = HomeState.PostCargados
                    }
                }
                else {
                    _state.value = HomeState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = HomeState.Error(R.string.Error_Network)
            }
        }
    }
}
