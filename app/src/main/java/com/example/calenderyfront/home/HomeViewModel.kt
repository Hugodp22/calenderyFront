package com.example.calenderyfront.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Home
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.pageSize
import com.example.calenderyfront.profile.ProfileState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.reflect.typeOf

class HomeViewModel(path: SavedStateHandle): ViewModel(){

    private val userInfo = path.toRoute<Home>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _uiState = MutableStateFlow(HomeUiState(userInfo))
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _state = MutableStateFlow<HomeState>(HomeState.Iniciado)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var currentPagePosts = 0
    private var currentPageComments = 0
    private val currentPageSize = pageSize


    init {
        //loadPosts()
    }

    fun loadPosts() {
        val currentUiState = _uiState.value

        if (_state.value is HomeState.Cargando || currentUiState.ultimaPaginaPost) {
            return
        }
        _state.value = HomeState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val respuesta = RetrofitClient.publicacionApi.obtenerPublicacionesHome(currentPagePosts, pageSize)

                if (respuesta.isSuccessful) {
                    val listaObtenida = respuesta.body()

                    if (listaObtenida != null) {
                        _uiState.update {
                            it.copy(
                                posts = it.posts + listaObtenida,
                                )
                        }
                        currentPagePosts++
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

    fun getCommentsPost(idPost: Int) {
        val currentState = _uiState.value

        if (_state.value is HomeState.Cargando || currentState.ultimaPaginaComment) {
            return
        }

        _state.value = HomeState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.obtenerComentariosPublicacion(
                    idPublicacion = idPost,
                    page = currentPageComments,
                    size = currentPageSize
                )

                if (respuesta.isSuccessful) {
                    val comentariosCargados = respuesta.body()

                    if (comentariosCargados != null) {
                        _uiState.update { it.copy(
                            listComments = it.listComments + comentariosCargados.content,
                            ultimaPaginaComment = comentariosCargados.content.size < currentPageSize
                        )}
                        _state.value = HomeState.PaginaCargada
                        currentPageComments++
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

    fun sendCommentToPost(idPost: Int) {
        val currentState = _uiState.value

        if (currentState.comment.isEmpty()) {
            _state.value = HomeState.Error(R.string.Error_comment_send)
            return
        }

        _state.value = HomeState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.enviarComentarioPublicacion(
                    idUsuario = userInfo.idUsuario,
                    idPublicacion = idPost,
                    comentario = currentState.comment
                )

                if (respuesta.isSuccessful) {
                    _uiState.update { it.copy(comment = "") }
                    _state.value = HomeState.Iniciado
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

    fun deleteCommentsLoaded() {
        currentPageComments = 0
        _uiState.update { it.copy(ultimaPaginaComment = false, listComments = emptyList()) }
    }
}
