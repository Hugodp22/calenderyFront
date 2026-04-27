package com.example.calenderyfront.profile

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

class ProfileViewModel(path: SavedStateHandle): ViewModel() {

    private val userInfo = path.toRoute<Settings>(
        typeMap = mapOf(typeOf<UserInfo>() to UserInfoNavType)
    ).userInfo

    private val _uiState = MutableStateFlow(ProfileUiState(userInfo, "", "Perfil_defecto.png", "", 0, 0))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Iniciado)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var currentPage = 0
    private val currentPageSize = pageSize

    private var lastLoadedYear: Int? = null

    private var lastLoadedMonth: Int? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val respuesta = RetrofitClient.usuarioApi.buscarDatosPerfil(userInfo.idUsuario)

                if (respuesta.isSuccessful) {
                    val userData = respuesta.body()

                    if (userData != null) {
                        _uiState.update {
                            it.copy(
                                nombreUsuario = userData.nombre,
                                fotoUsuario = userData.fotoPerfil + "?width=500&quality=7",
                                descripcion = userData.descripcion,
                                cantidadSeguidos = userData.cantidadSeguidos,
                                cantidadSeguidores = userData.cantidadSeguidores
                            )
                        }
                        //loadPublications()
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

        // Si ya esta cargando la peticion o si ya no hay mas que cargar, paramos
        if (_state.value is ProfileState.Cargando || currentState.ultimaPagina) {
            return
        }

        //Si cambiamos de mes en el perfil, reseteamos todo
        if (year != lastLoadedYear || month != lastLoadedMonth) {
            currentPage = 0
            _uiState.update { it.copy(ultimaPagina = false, publicaciones = emptyList()) }
            lastLoadedYear = year
            lastLoadedMonth = month
        }

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ProfileState.Cargando
            try {
                //Pedimos mediante el id, una pagina con tal tamaño de publicaciones, indicando el mes y el año
                val respuesta = RetrofitClient.publicacionApi.obtenerPublicacionesPerfil(
                    userId = userInfo.idUsuario,
                    month = month,
                    year = year,
                    page = currentPage,
                    size = currentPageSize,
                )

                if (respuesta.isSuccessful) {
                    val publicacionesCargadas = respuesta.body()

                    if (publicacionesCargadas != null) {
                        _uiState.update { it.copy(
                            publicaciones = it.publicaciones + publicacionesCargadas,

                            //Si nos devuelven menos del tamaño de cada pagina, es que ya no hay mas en el server
                            //asi que lo guardamos para evitar hacer peticiones de mas
                            ultimaPagina = publicacionesCargadas.size < currentPageSize
                        )}
                        _state.value = ProfileState.PaginaCargada
                        currentPage++
                    }
                    else {
                        _state.value = ProfileState.Error(errorMessages(respuesta.code()))
                    }
                }
            }
            catch (e: Exception) {
                _state.value = ProfileState.Error(R.string.Error_Network)
            }
        }
    }
}
