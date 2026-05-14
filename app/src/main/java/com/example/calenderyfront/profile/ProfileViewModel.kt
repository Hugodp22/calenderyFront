package com.example.calenderyfront.profile

import android.annotation.SuppressLint
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.calenderyfront.Model.DataObjects.ChatDto
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
import kotlinx.coroutines.delay
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

    private val _uiState = MutableStateFlow(ProfileUiState(userInfo, otherUserId, mainId,null,"", "Perfil_defecto.png", ""))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Iniciado)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var currentPagePosts = 0
    private var currentPageComments = 0
    private val currentPageSize = pageSize
    private var lastLoadedYear: Int? = null
    private var lastLoadedMonth: Int? = null

    private var searchJobMonth: Job? = null

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
                                existeChat = userData.existeChat
                            )
                        }
                        if (mainId != userInfo.idUsuario) {
                            loadMyData()
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

    fun loadMyData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.obtenerMisVisuales()

                if (respuesta.isSuccessful) {
                    val userData = respuesta.body()

                    if (userData != null) {
                        _uiState.update {
                            it.copy(
                                miNombre = userData.nombreUsuario,
                                miFoto = userData.fotoPerfil
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

        searchJobMonth?.cancel()

        searchJobMonth = viewModelScope.launch(Dispatchers.IO) {
            delay(550)
            _state.value = ProfileState.Cargando
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

                        if (publicacionesCargadas.content.isEmpty()) {
                            _state.value = ProfileState.NoPublicaciones
                        }
                        else {
                            _uiState.update { it.copy(
                                publicaciones = it.publicaciones + publicacionesCargadas.content,
                                ultimaPaginaPosts = publicacionesCargadas.content.size < currentPageSize
                            )}
                            _state.value = ProfileState.PaginaCargada
                            currentPagePosts++
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

    fun updateFollowVisuals(follow: Boolean) {
        _uiState.update { it.copy(
            cantidadSeguidores = if (follow) it.cantidadSeguidores + 1 else it.cantidadSeguidores - 1,
            seguidor = follow
        )}
    }

    fun followUser() {
        val currentState = _uiState.value

        if (currentState.seguidor) {
            return
        }

        updateFollowVisuals(follow = true)

        _state.value = ProfileState.Siguiendo

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.seguirUsuario(userInfo.idUsuario,mainId)

                if (respuesta.isSuccessful) {
                    _state.value = ProfileState.Iniciado
                }

                else {
                    updateFollowVisuals(follow = false)
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                updateFollowVisuals(follow = false)
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun unFollowUser() {
        val currentState = _uiState.value

        if (!currentState.seguidor) {
            return
        }

        updateFollowVisuals(follow = false)

        _state.value = ProfileState.Siguiendo

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.dejarDeSeguirUsuario(userInfo.idUsuario,mainId)

                if (respuesta.isSuccessful) {
                    _state.value = ProfileState.Iniciado
                }

                else {
                    updateFollowVisuals(follow = true)
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                updateFollowVisuals(follow = true)
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

                val respuesta = RetrofitClient.publicacionApi.enviarComentarioPublicacion(
                    PostCommentDto(
                        idPublicacion = idPost,
                        comentario = currentState.comment
                    )
                )

                if (respuesta.isSuccessful) {
                    val idComentario = respuesta.body()

                    if (idComentario != null) {
                        val newComment = Comment(
                            idUsuario = userInfo.idUsuario,
                            idComentario = idComentario,
                            nombreUsuario = currentState.miNombre ?: currentState.nombreUsuario,
                            fotoUsuario = currentState.miFoto ?: currentState.fotoUsuario,
                            comentario = currentState.comment
                        )

                        _uiState.update {
                            val postActualizados = currentState.publicaciones.map { postInList ->

                                if (postInList.id == idPost) {
                                    postInList.copy(cantidadComentarios = postInList.cantidadComentarios + 1)
                                }
                                else {
                                    postInList
                                }
                            }
                            val comentariosActualizados = listOf(newComment) + it.comentarios

                            currentState.copy(
                                publicaciones = postActualizados,
                                comentarios = comentariosActualizados,
                                comment = ""
                            )
                        }
                        _state.value = ProfileState.Iniciado
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

    fun deleteCommentsLoaded() {
        currentPageComments = 0
        _uiState.update { it.copy(
            ultimaPaginaComments = false,
            comentarios = emptyList(),
            comment = ""
        )}
    }

    fun changeLikesVisuals(post: PublicacionProfile,like: Boolean) {
        val currentState = _uiState.value

        _uiState.update {
            val listaActualizada = currentState.publicaciones.map { postInList ->

                if (postInList.id == post.id) {
                    postInList.copy(
                        like = like,
                        cantidadLikes = if (like) postInList.cantidadLikes + 1 else postInList.cantidadLikes - 1
                    )
                }
                else {
                    postInList
                }
            }
            currentState.copy(publicaciones = listaActualizada)
        }
    }

    fun likePost(post: PublicacionProfile) {
        if (post.like || _state.value is ProfileState.LikeCargando) {
            return
        }

        changeLikesVisuals(
            post = post,
            like = true
        )

        _state.value = ProfileState.LikeCargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.darLikePublicacion(idPublicacion = post.id)

                if (respuesta.isSuccessful) {
                    _state.value = ProfileState.Iniciado
                }
                else {
                    changeLikesVisuals(
                        post = post,
                        like = false
                    )
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                changeLikesVisuals(
                    post = post,
                    like = false
                )
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun unLikePost(post: PublicacionProfile) {
        if (!post.like || _state.value is ProfileState.LikeCargando) {
            return
        }

        changeLikesVisuals(
            post = post,
            like = false
        )

        _state.value = ProfileState.LikeCargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.publicacionApi.quitarLikePublicacion(idPublicacion = post.id)

                if (respuesta.isSuccessful) {
                    _state.value = ProfileState.Iniciado
                }

                else {
                    changeLikesVisuals(
                        post = post,
                        like = true
                    )
                    _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                }
            }
            catch (e: Exception) {
                changeLikesVisuals(
                    post = post,
                    like = true
                )
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }

    fun createChat(otherUserId: Int) {
        val currentState = _uiState.value

        if (_state.value is ProfileState.ChatCargando || currentState.existeChat) {
            return
        }

        _state.value = ProfileState.ChatCargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.chatApi.crearChatUsuario(ChatDto(
                    user2 = if (userInfo.idUsuario > otherUserId) userInfo.idUsuario else otherUserId,
                    user1 = if (userInfo.idUsuario > otherUserId) otherUserId else userInfo.idUsuario,
                    id = null
                ))

                if (respuesta.isSuccessful) {
                    val idChat = respuesta.body()
                    if (idChat != null) {
                        _uiState.update { it.copy(
                            existeChat = true,
                            chatId = idChat.idChat
                        )}
                        _state.value = ProfileState.ChatExito
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

    fun getChatId() {
        val currentState = _uiState.value

        if (_state.value is ProfileState.ChatCargando || !currentState.existeChat || otherUserId == null) {
            return
        }

        _state.value = ProfileState.ChatCargando

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.chatApi.obtenerIdChat(idUsuario = otherUserId)

                if (respuesta.isSuccessful) {
                    val idChat = respuesta.body()
                    if (idChat != null) {
                        _uiState.update { it.copy(
                            chatId = idChat.idChat
                        )}
                        _state.value = ProfileState.ChatExito
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

}
