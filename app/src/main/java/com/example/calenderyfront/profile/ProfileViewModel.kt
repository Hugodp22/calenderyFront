package com.example.calenderyfront.profile

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.Comment
import com.example.calenderyfront.Model.DataObjects.PostCommentDto
import com.example.calenderyfront.Model.DataObjects.Profile
import com.example.calenderyfront.Model.DataObjects.PublicacionProfile
import com.example.calenderyfront.Model.DataObjects.UserInfo
import com.example.calenderyfront.Model.DataObjects.UserInfoNavType
import com.example.calenderyfront.R
import com.example.calenderyfront.clients.RetrofitClient
import com.example.calenderyfront.errorMessages
import com.example.calenderyfront.pageSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class ProfileViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Profile>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val otherUserId = path.toRoute<Profile>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).otherUserID

    private val mainId: Int = otherUserId ?: userInfo.idUsuario  //Seleccionamos el Id con el que se van a cargar los datos

    private val _uiState = MutableStateFlow(ProfileUiState(userInfo, otherUserId, mainId,"", "Perfil_defecto.png", ""))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Iniciado)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var currentPagePosts = 0
    private var currentPageComments = 0
    private val currentPageSize = pageSize
    private var lastLoadedYear: Int? = null
    private var lastLoadedMonth: Int? = null

    private var searchJob: Job? = null

    init {
        //Para evitar errores si llegas a tu perfil mediante un comentario tuyo
        if (otherUserId == userInfo.idUsuario) {
            _uiState.update { it.copy(otherUserId = null) }
        }
        loadProfile()
    }

    fun onCommentChange(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    private fun loadProfile() {
        val currentUiState = _uiState.value
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.buscarDatosPerfil(currentUiState.mainId)

                if (respuesta.isSuccessful) {
                    val userData = respuesta.body()

                    if (userData != null) {
                        _uiState.update {
                            it.copy(
                                nombreUsuario = userData.nombre,
                                fotoUsuario = userData.fotoPerfil,
                                descripcion = userData.descripcion,
                                cantidadSeguidos = userData.cantidadSeguidos,
                                cantidadSeguidores = userData.cantidadSeguidores,
                                seguidor = userData.seguidor,
                            )
                        }
                    }
                    else {
                        _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                    }
                }
                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun loadPublicationsByDate(year: Int, month: Int) {
        val currentState = _uiState.value

        if (year != lastLoadedYear || month != lastLoadedMonth) {
            currentPagePosts = 0
            _uiState.update { it.copy(ultimaPaginaPosts = false, publicaciones = emptyList()) }
            lastLoadedYear = year
            lastLoadedMonth = month
            _state.value = ProfileState.Iniciado
        }

        // Si ya esta cargando la peticion o si ya no hay mas que cargar, paramos
        if (_state.value is ProfileState.Cargando || currentState.ultimaPaginaPosts) {
            return
        }

        _state.value = ProfileState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Pedimos mediante el id, una pagina con tal tamaño de publicaciones, indicando el mes y el año
                val respuesta = RetrofitClient.publicacionApi.obtenerPublicacionesPerfil(
                    idUsuario = currentState.mainId,
                    month = month,
                    year = year,
                    page = currentPagePosts,
                    size = currentPageSize
                )

                if (respuesta.isSuccessful) {
                    val publicacionesCargadas = respuesta.body()

                    if (publicacionesCargadas != null) {
                        _uiState.update { it.copy(
                            publicaciones = it.publicaciones + publicacionesCargadas.content,

                            //Si nos devuelven menos del tamaño de cada pagina, es que ya no hay mas en el server
                            //asi que lo guardamos para evitar hacer peticiones de mas
                            ultimaPaginaPosts = publicacionesCargadas.content.size < currentPageSize
                        )}
                        _state.value = ProfileState.PaginaCargada
                        currentPagePosts++
                    }
                    else {
                        _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                    }
                }
                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }
    fun followUser() {
        val currentState = _uiState.value

        if (currentState.seguidor) {
            return
        }

        _state.value = ProfileState.Siguiendo

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.seguirUsuario(userInfo.idUsuario,mainId)

                if (respuesta.isSuccessful) {
                    _uiState.update { it.copy(
                        cantidadSeguidores = it.cantidadSeguidores + 1,
                        seguidor = true
                    )}
                    _state.value = ProfileState.Iniciado
                }

                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun unFollowUser() {
        val currentState = _uiState.value

        if (!currentState.seguidor) {
            return
        }

        _state.value = ProfileState.Siguiendo

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.dejarDeSeguirUsuario(userInfo.idUsuario,mainId)

                if (respuesta.isSuccessful) {
                    _uiState.update { it.copy(
                        cantidadSeguidores = it.cantidadSeguidores - 1,
                        seguidor = false
                    )}
                    _state.value = ProfileState.Iniciado
                }

                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getCommentsPost(idPost: Int) {
        val currentState = _uiState.value

        if (_state.value is ProfileState.Cargando || currentState.ultimaPaginaComments) {
            return
        }

        _state.value = ProfileState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.obtenerComentariosPublicacion(
                    publicacionId = idPost,
                    page = currentPageComments,
                    size = currentPageSize
                )

                    if (respuesta.isSuccessful) {
                        val comentariosCargados = respuesta.body()

                        if (comentariosCargados != null) {
                            _uiState.update { it.copy(
                                comentarios = it.comentarios + comentariosCargados.content,
                                ultimaPaginaComments = comentariosCargados.content.size < currentPageSize
                            )}
                            _state.value = ProfileState.PaginaCargada
                            currentPageComments++
                        }
                    }
                    else {
                        _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                    }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun sendCommentToPost(idPost: Int) {
        val currentState = _uiState.value

        if (currentState.comment.isEmpty()) {
            _state.value = ProfileState.Error(R.string.Error_comment_send)
            return
        }

        _state.value = ProfileState.Cargando

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val respuesta = RetrofitClient.publicacionApi.enviarComentarioPublicacion(PostCommentDto(
                        idPublicacion = idPost,
                        comentario = currentState.comment
                    )
                )

                if (respuesta.isSuccessful) {
                    _uiState.update { it.copy(
                        comentarios = it.comentarios,
                        comment = ""
                    )}
                    _state.value = ProfileState.Iniciado
                }

                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun deleteCommentsLoaded() {
        currentPageComments = 0
        _uiState.update { it.copy(
            ultimaPaginaComments = false,
            comentarios = emptyList(),
            comment = ""
        )}
    }

    fun likePost(post: PublicacionProfile) {
        val currentState = _uiState.value

        if (post.like) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.darLikePublicacion(userInfo.idUsuario,post.id)

                if (respuesta.isSuccessful) {
                    _uiState.update { currentState ->
                        val listaActualizada = currentState.publicaciones.map { postList ->

                            if (postList.id == post.id) {
                                postList.copy(like = true, cantidadLikes = postList.cantidadLikes + 1)
                            }

                            else {
                                postList
                            }
                        }
                        currentState.copy(publicaciones = listaActualizada)
                    }
                    _state.value = ProfileState.Iniciado
                }

                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun unLikePost(post: PublicacionProfile) {
        val currentState = _uiState.value

        if (!post.like) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.quitarLikePublicacion(userInfo.idUsuario,post.id)

                if (respuesta.isSuccessful) {
                    _uiState.update { currentState ->
                        val listaActualizada = currentState.publicaciones.map { postList ->

                            if (postList.id == post.id) {
                                postList.copy(like = false, cantidadLikes = postList.cantidadLikes - 1)
                            }

                            else {
                                postList
                            }
                        }
                        currentState.copy(publicaciones = listaActualizada)
                    }
                    _state.value = ProfileState.Iniciado
                }

                else {
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }
}
